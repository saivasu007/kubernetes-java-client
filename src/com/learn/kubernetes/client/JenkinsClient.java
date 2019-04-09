package com.learn.kubernetes.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.dev.build.ibp.ibp_auth_sync.model.Crumb;
import com.intuit.dev.build.ibp.ibp_auth_sync.model.RoleType;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class JenkinsClient {
	private static final Logger logger = LoggerFactory.getLogger(JenkinsClient.class);
	private static final String CRUMB_URI = "crumbIssuer/api/json";
	private static final String GET_ALL_ROLES = "role-strategy/strategy/getAllRoles";
	private static final String CREATE_ROLE = "role-strategy/strategy/addRole";
	private static final String ASSIGN_ROLE = "role-strategy/strategy/assignRole";
	private static final String REMOVE_ROLE = "role-strategy/strategy/removeRoles";
	private static final String UNASSIGN_ROLE = "role-strategy/strategy/unassignRole";
	private static final String REMOVE_USER = "role-strategy/strategy/deleteSid";
	private static final JenkinsClient jenkinsClient = new JenkinsClient();

	public static JenkinsClient getJenkinsClient() {
		return jenkinsClient;
	}

    /**
     * Get the crumb given the master
	 * @param url
     * @param master
     * @return
     * @throws UnirestException
     */
	public Crumb getCrumb(String url, String master) throws UnirestException {

		if (master == null || master.isEmpty()) {
			throw new IllegalArgumentException("Invalid master");
		}
		master = master.trim();
		logger.info("Get Crumb for master :" + url + master);
		String jenkinsURL = buildJenkinsURL(url);
		Crumb crumb = null;

		HttpResponse<Crumb> response = null;
		try {
			response = Unirest.get(jenkinsURL)
					.routeParam("master", master)//replace master in the url
					.routeParam("uri", CRUMB_URI)
					.basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
					.asObject(Crumb.class);

		} catch (Exception e) {
			logger.error("The request to get crumb body from response for master: " + url + master + " failed  Status: "
                    + response.getStatus(), e);
		}

        if (response.getStatus() != 200) {
            throw new IllegalArgumentException("Failed to Get Crumb for master :" + url + master + " Status: " + response.getStatus());
        }

		return response.getBody();

	}

    /**
     * Get all the jenkins roles from a given jenkins master
	 * @param url
     * @param master
     * @param crumb
     * @return
     * @throws UnirestException
     * @throws IOException
     */
	public Set<String> getAllRoles(String url, String master, Crumb crumb) throws UnirestException {

		if (master == null || master.isEmpty()) {
			throw new IllegalArgumentException("Invalid master");
		}

		logger.info("Get List of Roles for master: " + url + master);

		String jenkinsURL = buildJenkinsURL(url);

        url = "https://build-dev.intuit.com/qa/role-strategy/strategy/getAllRoles";

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        String auth = getJenkinsUser(master) + ":" + getJenkinsAPIToken(master);

        byte[] message = auth.getBytes(StandardCharsets.UTF_8);

        // add request header
        request.addHeader(crumb.getCrumbRequestField(), crumb.getCrumb());
        request.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(message));

        org.apache.http.HttpResponse res = null;
        try {
            res = client.execute(request);
        } catch (Exception ex) {
            logger.error("Failed to Get List of Roles for master: " + url + master +  " failed", ex);
        }

        BufferedReader rd = null;
        StringBuffer result = new StringBuffer();
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();

        try {
            rd = new BufferedReader(
                    new InputStreamReader(res.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);

                String resp = null;

                resp = result.toString();
                ObjectMapper mapper = new ObjectMapper();

                // converting JSON string to Map
                map = mapper.readValue(resp, new TypeReference<Map<String, List<Object>>>() {
                });
            }
        }
        catch(IOException ex)
        {
            logger.error("Not able to map the JSON response to a set of roles", ex);
        }

		logger.info("List of roles for master: " + url + master + " are : " + map.keySet());

		return map.keySet();
	}

    /**
     * Create a specific type of role having a set of permissions and pattern for that role
	 * @param url
     * @param master
     * @param roleType
     * @param roleName
     * @param pattern
     * @param permission
     * @param overWrite
     * @param crumb
     * @throws UnirestException
     */
    public void createRole(String url, String master, RoleType roleType, String roleName, String pattern, String permission,
						   boolean overWrite, Crumb crumb) throws UnirestException {

		if (master == null || master.isEmpty()) {
			throw new IllegalArgumentException("Invalid master");
		}
		if (roleName == null || roleName.isEmpty()) {
			throw new IllegalArgumentException("Invalid role name");
		}

		if (permission == null || permission.isEmpty()) {
			throw new IllegalArgumentException("Invalid permission");
		}

		logger.info("Creating Role: " + roleName + " for jenkins master: " + url + master);

		String jenkinsURL = buildJenkinsURL(url);

		HttpResponse<JsonNode> response = null;

		Map<String, Object> fields = new HashMap<>();
		fields.put("type", roleType.getName());
		fields.put("roleName", roleName);
		fields.put("permissionIds", permission);
		fields.put("overwrite", overWrite);

        //if(pattern != null) {
        //    fields.put("pattern", pattern); //only required for projectroles
        //}

		try {
			response = Unirest.post(jenkinsURL)
					.routeParam("master", master)//replace master in the url
					.routeParam("uri", CREATE_ROLE)
					.header(crumb.getCrumbRequestField(), crumb.getCrumb())
					.basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
					.fields(fields)
					.asJson();
		} catch (Exception e) {
			logger.error("The request to create roles for jenkins master: " + jenkinsURL + master + " failed " + response.getBody(), e);
		}
		if (response.getStatus() != 200) {
			throw new IllegalArgumentException("Role creation failed: " + roleName + " Status: " + response.getStatus());
		}

        logger.info("Created Role: " + roleName + " for jenkins master: " + url + master);

    }

    /**
     * Assign a specific type of role to the user given the userName
	 * @param url
     * @param master
     * @param roleType
     * @param roleName
     * @param userName
     * @param crumb
     * @throws UnirestException
     */
	public void assignRole(String url, String master, RoleType roleType, String roleName, String userName, Crumb crumb)
			throws UnirestException {

		logger.info("Assigning Role: " + roleName + ", for jenkins master: " + url + master + " for userName: " + userName);

		String jenkinsURL = buildJenkinsURL(url);

        HttpResponse<JsonNode> response = null;
		try {
		response = Unirest.post(jenkinsURL)
				.routeParam("master", master)//replace master in the url
				.routeParam("uri", ASSIGN_ROLE)
				.header(crumb.getCrumbRequestField(), crumb.getCrumb())
				.basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
				.field("type", roleType.getName())
				.field("roleName", roleName)
				.field("sid", userName)
				.asJson();
	    } catch (Exception e) {
			logger.error("Failed Assigning Role : " + roleName + ", for jenkins master: " + url + master + " for userName: "
                    + userName, e);
	    }

		if (response.getStatus() != 200) {
			throw new IllegalArgumentException("Assigning Role: " + roleName + ", for jenkins master: " + url + master + " for userName: "
                    + userName + " Status: " + response.getStatus());
		}

        logger.info("Assigned Role: " + roleName + ", for jenkins master: " + url + master + " for userName: " + userName);

    }

	/**
	 * Remove a specific role from the jenkins master
	 * @param url
	 * @param crumb
	 * @param master
	 * @param roleType
	 * @param roleName
	 * @throws UnirestException
	 */
	public void removeRole(String url, Crumb crumb, String master, RoleType roleType, String roleName) throws UnirestException {

        logger.info("Removing Role: " + roleName + ", for jenkins master: " + url + master);

		String jenkinsURL = buildJenkinsURL(url);

        HttpResponse<JsonNode> response = null;

        try {
           response = Unirest.post(jenkinsURL)
                    .routeParam("master", master)//replace master in the url
                    .routeParam("uri", REMOVE_ROLE)
                    .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                    .basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
                    .field("type", roleType.getName())
                    .field("roleNames", String.join(",", roleName))
                    .asJson();
        } catch (Exception e) {
            logger.error("The request to remove roles for jenkins master: " + url + master + " failed", e);
        }

        if (response.getStatus() != 200) {
            throw new IllegalArgumentException("Failed Removing Role: " + roleName + ", for jenkins master: " + url + master +
                    " Status: " + response.getStatus());
        }

        logger.info("Removed Role: " + roleName + ", for jenkins master: " + url + master);

    }

    /**
     * Remove a user from a specific role given the userName
	 * @param url
     * @param crumb
     * @param master
     * @param roleType
     * @param userName
     * @throws UnirestException
     */
	public void removeUser(String url, Crumb crumb, String master, RoleType roleType, String userName) throws UnirestException {

        logger.info("Removing User: " + userName + ", for jenkins master: " + url + master);

		String jenkinsURL = buildJenkinsURL(url);

        HttpResponse<JsonNode> response = null;

		try {
            response = Unirest.post(jenkinsURL)
                    .routeParam("master", master)//replace master in the url
                    .routeParam("uri", REMOVE_USER)
                    .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                    .basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
                    .field("type", roleType.getName())
                    .field("sid", userName)
                    .asJson();
        } catch (Exception e) {
        logger.error("The request to remove user: " +userName+  " for jenkins master: " + url + master + " failed", e);
        }

        if (response.getStatus() != 200) {
            throw new IllegalArgumentException("Failed Removing User: " + userName + ", for jenkins master: " + url + master +
                    " Status: " + response.getStatus());
        }
        logger.info("Removed User: " + userName + ", for jenkins master: " + url + master);

    }

    /**
     * Un assign a specific type of role for the user given the userName
	 * @param url
     * @param master
     * @param roleType
     * @param roleName
     * @param userName
     * @param crumb
     * @throws UnirestException
     */
    public void unassignRole(String url, String master, RoleType roleType, String roleName, String userName, Crumb crumb)
			throws UnirestException {

		logger.info("Unassigning Role: " + roleName + ", userName: " + userName + " for jenkins master: " + url + master);

		String jenkinsURL = buildJenkinsURL(url);

        HttpResponse<JsonNode> response = null;

		try {
            response = Unirest.post(jenkinsURL)
                    .routeParam("master", master)//replace master in the url
                    .routeParam("uri", UNASSIGN_ROLE)
                    .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                    .basicAuth(getJenkinsUser(master), getJenkinsAPIToken(master))
                    .field("type", roleType.getName())
                    .field("roleName", roleName)
                    .field("sid", userName)
                    .asJson();
        } catch (Exception e) {
            logger.error("The request to unassign role: " +roleName+ " for jenkins master: " + url + master + " failed", e);
        }

        if (response.getStatus() != 200) {
            throw new IllegalArgumentException("Failed Unassigning Role: " + roleName + ", userName: " + userName + " for jenkins master: "
                    + url + master + " Status: " + response.getStatus());
        }

        logger.info("Unassigned Role: " + roleName + ", userName: " + userName + " for jenkins master: " + url + master);

    }

	/**
	 * Build the jenkins URL string for Unirest route Param
	 * @param url
	 * @return
	 */
	private String buildJenkinsURL(String url) {
			return url + "{master}/{uri}";
	}

	protected String getJenkinsUser(String master) {
		return IBPSyncInitializer.prop.getProperty("jenkins.user." + master);
	}

	protected String getJenkinsAPIToken(String master) {
		return IBPSyncInitializer.prop.getProperty("jenkins.apitoken." + master);
	}

	private JenkinsClient() {
	}
}
