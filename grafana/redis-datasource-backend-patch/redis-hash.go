package main

import (
	"fmt"
	"sort"
	"strconv"
	"strings"

	"bitbucket.org/creachadair/shell"
	"github.com/grafana/grafana-plugin-sdk-go/backend"
	"github.com/grafana/grafana-plugin-sdk-go/data"
)

/**
 * Expand keys based on the provided pattern using SCAN functionality.
 */
func expandKeys(client redisClient, pattern string) ([]string, error) {
	if !strings.ContainsAny(pattern, "*?[]") {
		return []string{pattern}, nil
	}
	var (
		cursor = "0"
		keys   []string
	)
	for {
		var result []interface{}
		err := client.RunFlatCmd(&result, "SCAN", cursor, "MATCH", pattern, "COUNT", 100)
		if err != nil {
			return nil, err
		}
		cursor = string(result[0].([]byte))
		for _, k := range result[1].([]interface{}) {
			keys = append(keys, string(k.([]byte)))
		}
		if cursor == "0" {
			break
		}
	}
	return keys, nil
}

/**
 * HGETALL key
 *
 * @see https://redis.io/commands/hgetall
 */
func queryHGetAll(qm queryModel, client redisClient) backend.DataResponse {
	response := backend.DataResponse{}

	keys, err := expandKeys(client, qm.Key)
	if err != nil {
		return errorHandler(response, err)
	}

	// Check if there is only one key and no wildcards
	if len(keys) == 1 && !strings.ContainsAny(qm.Key, "*?[]") {
		// Execute command
		var result []string
		err := client.RunFlatCmd(&result, qm.Command, qm.Key)

		// Check error
		if err != nil {
			return errorHandler(response, err)
		}

		// New Frame
		frame := data.NewFrame(qm.Command)

		// Add fields and values
		for i := 0; i < len(result); i += 2 {
			if floatValue, err := strconv.ParseFloat(result[i+1], 64); err == nil {
				frame.Fields = append(frame.Fields, data.NewField(result[i], nil, []float64{floatValue}))
			} else {
				frame.Fields = append(frame.Fields, data.NewField(result[i], nil, []string{result[i+1]}))
			}
		}

		// Add the frames to the response
		response.Frames = append(response.Frames, frame)

		// Return
		return response
	} else {
		// Collect all fields across all hashes
		fieldSet := map[string]struct{}{}
		keyToFields := map[string]map[string]string{}
		for _, key := range keys {
			var result []string
			err := client.RunFlatCmd(&result, qm.Command, key)
			if err != nil {
				return errorHandler(response, err)
			}
			fields := map[string]string{}
			for i := 0; i < len(result); i += 2 {
				fields[result[i]] = result[i+1]
				fieldSet[result[i]] = struct{}{}
			}
			keyToFields[key] = fields
		}

		// Build frame columns
		fieldNames := []string{"key"}
		var extraFields []string
		for f := range fieldSet {
			extraFields = append(extraFields, f)
		}
		sort.Strings(extraFields)
		fieldNames = append(fieldNames, extraFields...)

		cols := make([][]string, len(fieldNames))
		for i := range cols {
			cols[i] = make([]string, 0, len(keys))
		}

		// Fill columns
		for _, key := range keys {
			cols[0] = append(cols[0], key)
			fields := keyToFields[key]
			for i, f := range fieldNames[1:] {
				cols[i+1] = append(cols[i+1], fields[f])
			}
		}

		// Create frame
		frame := data.NewFrame("HGETALL")
		for i, name := range fieldNames {
			// Try to parse all values as float64
			floatVals := make([]float64, len(cols[i]))
			allFloat := true
			for j, v := range cols[i] {
				if v == "" {
					floatVals[j] = 0
					continue
				}
				f, err := strconv.ParseFloat(v, 64)
				if err != nil {
					allFloat = false
					break
				}
				floatVals[j] = f
			}
			if allFloat {
				frame.Fields = append(frame.Fields, data.NewField(name, nil, floatVals))
			} else {
				frame.Fields = append(frame.Fields, data.NewField(name, nil, cols[i]))
			}
		}
		response.Frames = append(response.Frames, frame)
		return response
	}
}

/**
 * HGET key field
 *
 * @see https://redis.io/commands/hget
 */
func queryHGet(qm queryModel, client redisClient) backend.DataResponse {
	response := backend.DataResponse{}

	// Execute command
	var value string
	err := client.RunFlatCmd(&value, qm.Command, qm.Key, qm.Field)

	// Check error
	if err != nil {
		return errorHandler(response, err)
	}

	// Add the frames to the response
	response.Frames = append(response.Frames, createFrameValue(qm.Field, value, qm.Field))

	// Return
	return response
}

/**
 * HMGET key field [field ...]
 *
 * @see https://redis.io/commands/hmget
 */
func queryHMGet(qm queryModel, client redisClient) backend.DataResponse {
	response := backend.DataResponse{}

	// Split Field to array
	fields, ok := shell.Split(qm.Field)

	// Check if filter is valid
	if !ok {
		response.Error = fmt.Errorf("field is not valid")
		return response
	}

	// Execute command
	var result []string
	err := client.RunFlatCmd(&result, qm.Command, qm.Key, fields)

	// Check error
	if err != nil {
		return errorHandler(response, err)
	}

	// New Frame
	frame := data.NewFrame(qm.Command)

	// Parse results and add fields
	for i, value := range result {
		if floatValue, err := strconv.ParseFloat(value, 64); err == nil {
			frame.Fields = append(frame.Fields, data.NewField(fields[i], nil, []float64{floatValue}))
		} else {
			frame.Fields = append(frame.Fields, data.NewField(fields[i], nil, []string{value}))
		}
	}

	// Add the frames to the response
	response.Frames = append(response.Frames, frame)

	// Return
	return response
}
