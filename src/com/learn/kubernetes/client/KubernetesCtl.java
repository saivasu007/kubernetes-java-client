package com.learn.kubernetes.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import dnl.utils.text.table.TextTable;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapList;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1Secret;
import io.kubernetes.client.models.V1SecretList;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceAccount;
import io.kubernetes.client.models.V1ServiceAccountList;
import io.kubernetes.client.models.V1ServiceList;
import io.kubernetes.client.util.Config;

public class KubernetesCtl {

	static CoreV1Api api;
	static AppsV1Api appsApi;
	static List<List<String>> results = new ArrayList<List<String>>();

	public static CoreV1Api getCoreV1Api() throws Exception {
		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		return new CoreV1Api();
	}
	
	public static AppsV1Api getAppsV1Api() throws Exception {
		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		return new AppsV1Api();
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		System.out.println("*************** Welcome to Kubernetes Eco System *************** ");
		System.out.println(" 1. nodes");
		System.out.println(" 2. namespaces");
		System.out.println(" 3. services");
		System.out.println(" 4. deployments");
		System.out.println(" 5. pods");
		System.out.println(" 6. secrets");
		System.out.println(" 7. configmaps");
		System.out.println(" 8. serviceaccounts");
		System.out.println("");
		Scanner input = new Scanner(System.in);
		System.out.println("##########################################################");
		System.out.println("Enter the Kube Input (As given above) to list the Objects.");
		System.out.println("##########################################################");
		System.out.println("");
		String option = input.nextLine().toLowerCase();
		switch (option) {
		case "":
			System.out.println("No Option Selected, So listing all Cluster Objects");
			getAllKubeObjects();
			break;
		case "nodes":
			getNodes();
			break;
		case "namespaces":
			getNamespaces();
			break;
		case "services":
			getServicesByNamespace("");
			break;
		case "deployments":
			getDeploymentsByNamespace("");
			break;
		case "pods":
			getPodsByNamespace("");
			break;
		case "secrets":
			getSecretsByNamespace("");
			break;
		case "configmaps":
			getConfigMapsByNamespace("");
		case "serviceaccounts":
			// getServicesByNamespace("");
			break;
		case "pretty":
			printPretty(null, null);
			break;
		case "rawoutput" :
			getRawOutput();
			break;
		default:
			System.out.println("Please Recheck your input and Try again [Enter only string input]");
			break;
		}

	}

	public static void getAllKubeObjects() throws Exception {
		api = getCoreV1Api();

		V1NodeList nodeList = api.listNode(null, null, null, false, null, null, null, null, false);
		System.out.println("#################################");
		System.out.println("List of NODES in the cluster");
		System.out.println("#################################");
		for (V1Node item : nodeList.getItems()) {
			System.out.println(
					item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getStatus().getNodeInfo().getOperatingSystem()
							+ "\t\t\t\t\t\t" + item.getMetadata().getSelfLink());
		}

		System.out.println("#################################");
		System.out.println("List of Namespaces in the cluster");
		System.out.println("#################################");
		V1NamespaceList nsList = api.listNamespace(null, null, null, null, null, null, null, null, null);
		for (V1Namespace item : nsList.getItems()) {
			System.out.println(item.getMetadata().getName());
		}

		V1ServiceList svcList = api.listServiceForAllNamespaces(null, null, false, null, null, null, null, null, false);
		System.out.println("#################################");
		System.out.println("List of SERVICES in the cluster");
		System.out.println("#################################");
		for (V1Service item : svcList.getItems()) {
			System.out.println(
					item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getSpec().getPorts().get(0).getPort());
		}

		V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		System.out.println("#################################");
		System.out.println("List of PODS in the cluster");
		System.out.println("#################################");
		for (V1Pod item : list.getItems()) {
			System.out.println(item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getStatus().getPhase());
		}

		V1SecretList secList = api.listSecretForAllNamespaces(null, null, false, null, null, null, null, null, false);
		System.out.println("#################################");
		System.out.println("List of SECRETS in the cluster");
		System.out.println("#################################");
		for (V1Secret item : secList.getItems()) {
			System.out.println(item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getType() + "\t\t\t\t\t\t"
					+ item.getMetadata().getNamespace());
		}

		V1ConfigMapList configList = api.listConfigMapForAllNamespaces(null, null, false, null, null, null, null, null,
				false);
		System.out.println("#################################");
		System.out.println("List of CONFIGMAPS in the cluster");
		System.out.println("#################################");
		for (V1ConfigMap item : configList.getItems()) {
			System.out.println(item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getMetadata().getNamespace()
					+ "\t\t\t\t\t\t" + item.getMetadata().getSelfLink());
		}

		V1ServiceAccountList svcAcctList = api.listServiceAccountForAllNamespaces(null, null, false, null, null, null,
				null, null, false);
		System.out.println("#################################");
		System.out.println("List of SERVICE-ACCOUNTS in the cluster");
		System.out.println("#################################");
		for (V1ServiceAccount item : svcAcctList.getItems()) {
			System.out.println(item.getMetadata().getName() + "\t\t\t\t\t\t" + item.getMetadata().getNamespace()
					+ "\t\t\t\t\t\t" + item.getMetadata().getSelfLink());
		}

	}

	public static void getNodes() throws Exception {
		api = getCoreV1Api();
		String[] columnNames = { "Name", "Address", "OS", "Kernal ver", "Selflink"};
		V1NodeList nodeList = api.listNode(null, null, null, false, null, null, null, null, false);
		System.out.println("#################################");
		System.out.println("List of NODES in the cluster");
		System.out.println("#################################");
		for (V1Node item : nodeList.getItems()) {
			List<String> printList = new ArrayList<String>();
			printList.add(item.getMetadata().getName());
			printList.add(item.getStatus().getAddresses().get(0).getAddress());
			printList.add(item.getStatus().getNodeInfo().getOperatingSystem());
			printList.add(item.getStatus().getNodeInfo().getKernelVersion());
			printList.add(item.getMetadata().getSelfLink());
			results.add(printList);
		}
		printPretty(columnNames, results);
	}

	public static void getNamespaces() throws Exception {
		api = getCoreV1Api();
		String[] columnNames = { "Name", "Status", "Selflink" };
		System.out.println("#################################");
		System.out.println("List of Namespaces in the cluster");
		System.out.println("#################################");
		V1NamespaceList nsList = api.listNamespace(null, null, null, null, null, null, null, null, null);
		for (V1Namespace item : nsList.getItems()) {
			List<String> printList = new ArrayList<String>();
			printList.add(item.getMetadata().getName());
			printList.add(item.getStatus().getPhase());
			printList.add(item.getMetadata().getSelfLink());
			results.add(printList);
		}
		printPretty(columnNames, results);
	}

	@SuppressWarnings("resource")
	public static void getServicesByNamespace(String namespace) throws Exception {
		V1ServiceList svcList;
		Scanner input = new Scanner(System.in);
		String[] columnNames = { "Name", "Port", "Namespace" };
		System.out.println("Enter the Kube Namespace to list the Services.");
		namespace = input.nextLine().toLowerCase();
		api = getCoreV1Api();

		if (StringUtils.isNotBlank(namespace))
			svcList = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, false);
		else
			svcList = api.listServiceForAllNamespaces(null, null, false, null, null, null, null, null, false);
		if (svcList.getItems().size() > 0) {
			System.out.println("#################################");
			if (StringUtils.isNotBlank(namespace))
				System.out.println("List of SERVICES for Namespace : " + namespace + " in the cluster");
			else
				System.out.println("List of SERVICES from all Namespaces in the cluster");
			System.out.println("#################################");
			for (V1Service item : svcList.getItems()) {
				List<String> printList = new ArrayList<String>();
				printList.add(item.getMetadata().getName());
				printList.add(item.getSpec().getPorts().get(0).getPort().toString());
				printList.add(item.getMetadata().getNamespace());
				results.add(printList);
			}
			printPretty(columnNames, results);
		} else {
			System.out.println("There are no SERVICES available for Namespace : " + namespace + " in the cluster");
		}
	}
	
	@SuppressWarnings("resource")
	public static void getDeploymentsByNamespace(String namespace) throws Exception {
		V1DeploymentList depList;
		String[] columnNames = { "Name", "Replicas", "Container", "Image", "Namespace", "Selflink" };
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the Kube Namespace to list the Secrets.");
		namespace = input.nextLine().toLowerCase();
		appsApi = getAppsV1Api();

		if (StringUtils.isNotBlank(namespace))
			depList = appsApi.listNamespacedDeployment(namespace, null, null, null, null, null, null, null, null, false);
		else
			depList = appsApi.listDeploymentForAllNamespaces(null, null, false, null, null, null, null, null, false);
		if (depList.getItems().size() > 0) {
			System.out.println("#########################################################");
			if (StringUtils.isNotBlank(namespace))
				System.out.println("List of DEPLOYMENTS for Namespace : " + namespace + " in the cluster");
			else
				System.out.println("List of DEPLOYMENTS from all Namespaces in the cluster");
			System.out.println("#########################################################");
			for (V1Deployment item : depList.getItems()) {
				List<String> printList = new ArrayList<String>();
				printList.add(item.getMetadata().getName());
				printList.add(item.getStatus().getReplicas().toString());
				printList.add(item.getSpec().getTemplate().getSpec().getContainers().get(0).getName());
				printList.add(item.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
				printList.add(item.getMetadata().getNamespace());
				printList.add(item.getMetadata().getSelfLink());
				results.add(printList);
			}
			printPretty(columnNames, results);
		} else {
			System.out.println("There are no DEPLOYMENTS available for Namespace : " + namespace + " in the cluster");
		}
	}

	@SuppressWarnings("resource")
	public static void getPodsByNamespace(String namespace) throws Exception {
		V1PodList podList;
		Scanner input = new Scanner(System.in);
		String[] columnNames = { "Name", "Status", "Namespace" };
		System.out.println("Enter the Kube Namespace to list the Pods.");
		namespace = input.nextLine().toLowerCase();
		api = getCoreV1Api();

		if (StringUtils.isNotBlank(namespace))
			podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, false);
		else
			podList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		if (podList.getItems().size() > 0) {
			System.out.println("#########################################################");
			if (StringUtils.isNotBlank(namespace))
				System.out.println("List of PODS for Namespace : " + namespace + " in the cluster");
			else
				System.out.println("List of PODS from all Namespaces in the cluster");
			System.out.println("#########################################################");
			for (V1Pod item : podList.getItems()) {
				List<String> printList = new ArrayList<String>();
				printList.add(item.getMetadata().getName());
				printList.add(item.getStatus().getPhase());
				printList.add(item.getMetadata().getNamespace());
				results.add(printList);
			}
			printPretty(columnNames, results);
		} else {
			System.out.println("There are no PODS available for Namespace : " + namespace + " in the cluster");
		}
	}

	@SuppressWarnings("resource")
	public static void getSecretsByNamespace(String namespace) throws Exception {
		V1SecretList secList;
		String[] columnNames = { "Name", "Type", "Namespace" };
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the Kube Namespace to list the Secrets.");
		namespace = input.nextLine().toLowerCase();
		api = getCoreV1Api();

		if (StringUtils.isNotBlank(namespace))
			secList = api.listNamespacedSecret(namespace, null, null, null, null, null, null, null, null, false);
		else
			secList = api.listSecretForAllNamespaces(null, null, false, null, null, null, null, null, false);
		if (secList.getItems().size() > 0) {
			System.out.println("#########################################################");
			if (StringUtils.isNotBlank(namespace))
				System.out.println("List of SECRETS for Namespace : " + namespace + " in the cluster");
			else
				System.out.println("List of SECRETS from all Namespaces in the cluster");
			System.out.println("#########################################################");
			for (V1Secret item : secList.getItems()) {
				List<String> printList = new ArrayList<String>();
				printList.add(item.getMetadata().getName());
				printList.add(item.getType());
				printList.add(item.getMetadata().getNamespace());
				results.add(printList);
			}
			printPretty(columnNames, results);
		} else {
			System.out.println("There are no SECRETS available for Namespace : " + namespace + " in the cluster");
		}
	}

	@SuppressWarnings("resource")
	public static void getConfigMapsByNamespace(String namespace) throws Exception {
		V1ConfigMapList configList;
		String[] columnNames = { "Name", "Namespace", "SelfLink" };
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the Kube Namespace to list the Secrets.");
		namespace = input.nextLine().toLowerCase();
		api = getCoreV1Api();

		if (StringUtils.isNotBlank(namespace))
			configList = api.listNamespacedConfigMap(namespace, null, null, null, null, null, null, null, null, false);
		else
			configList = api.listConfigMapForAllNamespaces(null, null, false, null, null, null, null, null, false);
		if (configList.getItems().size() > 0) {
			System.out.println("#########################################################");
			if (StringUtils.isNotBlank(namespace))
				System.out.println("List of CONFIGMAPS for Namespace : " + namespace + " in the cluster");
			else
				System.out.println("List of CONFIGMAPS from all Namespaces in the cluster");
			System.out.println("#########################################################");
			for (V1ConfigMap item : configList.getItems()) {
				List<String> printList = new ArrayList<String>();
				printList.add(item.getMetadata().getName());
				printList.add(item.getMetadata().getNamespace());
				printList.add(item.getMetadata().getSelfLink());
				results.add(printList);
			}
			printPretty(columnNames, results);
		} else {
			System.out.println("There are no CONFIGMAPS available for Namespace : " + namespace + " in the cluster");
		}
	}

	public static void printPretty(String[] colNames, List<List<String>> output) {

		Object[][] array = new Object[output.size()][];
		int i = 0;
		for (List<String> event : output) {// each list
			array[i++] = event.toArray(new Object[event.size()]);
		}
		TextTable tt = new TextTable(colNames, array);
		tt.printTable();
	}
	
	private static void getRawOutput() throws Exception {
		V1PodList podList;
		api = getCoreV1Api();
		podList = api.listNamespacedPod("default", null, null, null, null, null, null, null, null, false);
		if (podList.getItems().size() > 0) {
			System.out.println("#########################################################");
			for (V1Pod item : podList.getItems()) {
				System.out.println(item.getMetadata());
				System.out.println(item.getStatus());
			}
			System.out.println("#########################################################");
		}
	}
}