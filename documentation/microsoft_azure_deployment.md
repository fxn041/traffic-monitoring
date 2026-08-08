# Deploying the Project to Microsoft Azure
This guide describes how to deploy the project to Azure Kubernetes Service (AKS) using Kubernetes.

## Prerequisites
To deploy to Azure, you need the following:
- The Azure CLI (`azure-cli`)
- The Kubernetes command-line tool (`kubectl`)
- An active Microsoft Azure account with a valid subscription

**On Linux (Debian-based):**
```sh
sudo apt-get update
sudo apt-get install -y azure-cli
sudo apt-get install -y kubectl
```

**On Windows:**
```sh
winget install -e --id Microsoft.AzureCLI
winget install -e --id Kubernetes.kubectl
```

## Setting Up the Kubernetes Cluster
1. **Log in to Azure:**  
    This command will prompt you to confirm your login in a browser.
    ```sh
    az login --use-device-code
    ```

2. **Check if the Container Service is Registered:**  
    ```sh
    az provider show --namespace Microsoft.ContainerService --query "registrationState"
    ```

3. **Register the Container Service (if needed):**  
    If the container service is not registered, run:
    ```sh
    az provider register --namespace Microsoft.ContainerService
    ```
    Wait until the registration is complete before proceeding. You can check the status using the previous command.

4. **Create a Resource Group:**  
    This group will contain your AKS cluster.
    ```sh
    az group create --name bigdata --location northeurope
    ```

5. **Create the AKS Cluster:**  
    This step may take several minutes. It will provision a virtual machine.  
    **Important:** Remember to delete the cluster when you are done to avoid unnecessary costs.
    ```sh
    az aks create --resource-group bigdata --name devCluster --node-count 1 --node-vm-size standard_e4ds_v4 --generate-ssh-keys
    ```

## Deploying the Project
1. **Get AKS Cluster Credentials:**  
    This command configures `kubectl` to connect to your new cluster.
    ```sh
    az aks get-credentials --resource-group bigdata --name devCluster
    ```

2. **Add the secrets:**
    The application requires a few secrets to run. For this a `.env` file is required, check the [documentation](env_setup.md) on how to set it up.
    Next, add the secrets to the cluster (run from the folder where you created your `.env` file in):
    ```sh
    kubectl create secret generic project-secret --from-env-file=.env
    ```

3. **Deploy the Application:**  
    Run this command from the root directory of your repository.
    ```sh
    kubectl apply -f kubernetes/minimal.yaml
    ```

    The project is now deployed. To find the external IP address, run:
    ```sh
    kubectl get svc
    ```

    The Grafana login is specified in your `.env` file.

## Cleanup and Deletion
When you no longer need the cluster, delete it to avoid additional charges:

1. **Delete the AKS Cluster:**  
    ```sh
    az aks delete --resource-group bigdata --name devCluster --yes
    ```

2. **Delete the Resource Group:**  
    ```sh
    az group delete --name bigdata --yes --no-wait
    ```
