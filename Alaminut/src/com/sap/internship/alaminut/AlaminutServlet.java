package com.sap.internship.alaminut;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * Servlet implementing a simple JDBC based persistence sample application for
 * SAP HANA Cloud Platform.
 */
public class AlaminutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AlaminutServlet.class);

	private ProductsDAO productsDAO;
	private RecipeDAO recipeDAO;
	private ConnProductDAO connProductDAO;
	

	/** {@inheritDoc} */
	@Override
	public void init() throws ServletException {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx
					.lookup("java:comp/env/jdbc/DefaultDB");
			
			productsDAO = new ProductsDAO(ds);
			recipeDAO = new RecipeDAO(ds);
			connProductDAO = new ConnProductDAO(ds);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		addBeginingHtml(response);
		
		try {
			response.getWriter().println("<p>Persistence with JDBC!</p>");
			response.getWriter().println(request.getRequestURI());
			appendRecipeTable(response);
			//appendAddForm(response);
		} catch(SQLException sql){
			response.getWriter().println("SQL Exception: " + sql);
		}catch (Exception e) {
			response.getWriter().println(
					"Persistence operation failed with reason: "
							+ e.getMessage());
			LOGGER.error("Persistence operation failed", e);
		}
		addEndingHtml(response);
	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			//doAdd(request);
			//doGet(request, response);
			
			
			doAddRecipe(request);
			doGet(request, response);
			
		} catch (Throwable e) {
			response.getWriter().println(
					"Persistence operation failed in post with reason: "
							+ e.getMessage());
			LOGGER.error("Persistence operation failed", e);
		}
	}
	
	private void doAddRecipe(HttpServletRequest request) throws SQLException{
		Map<String, String[]> parameterMap = request.getParameterMap();
		
		String recipeId = UUID.randomUUID().toString();
		String[] recipeName = parameterMap.get("recipe-name");
		String[] recipeHowToMake = parameterMap.get("recipe-howtomake");
		
		Recipe recipe = new Recipe(recipeId, recipeName[0], recipeHowToMake[0]);
		
		recipeDAO.addRecipe(recipe);
		ArrayList<String> addProductNEW = productsDAO.addProductNEW(parameterMap.get("product"));
		
		for (String product_id : addProductNEW) {
			connProductDAO.insertConnection(recipeId, product_id);
		}
	}

	/*
	private void appendRecipeForm(HttpServletResponse response)
			throws SQLException, IOException{
		response.getWriter()
			.println(
					"<div id=\"form-container\" style=\"width: 270px; border:1px solid black;\">"
					+ "<form id=\"product-form\" action=\"add\" method=\"post\">"
					+ "<label for=\"recipe-name\">Name</label><input type=\"text\" name=\"recipe-name\">"
					+ "<label for=\"recipe-howtomake\">How to make it</label>"
					+ "<textarea name=\"recipe-howtomake\" rows=\"15\" cols=\"35\"></textarea>"
					+ "<h3>Products:</h3>"
					+ "<input type=\"text\" id=\"product-1\"><button id=\"add-button\">+</button>"
					+ "<input type="
					+ "<input id=\"submit-button\" type=\"submit\" value=\"Add\">"
					+ "</form>"
					+ "</div>");
		addScriptsHtml(response);
	}

	private void appendAddForm(HttpServletResponse response) throws IOException {
		// Append form through which new persons can be added
		response.getWriter()
				.println(
						"<p><form action=\"\" method=\"post\">"
								+ "First name:<input type=\"text\" name=\"FirstName\">"
								+ "&nbsp;Last name:<input type=\"text\" name=\"LastName\">"
								+ "&nbsp;<input type=\"submit\" value=\"Add Person\">"
								+ "</form></p>");
	}

	private void doAdd(HttpServletRequest request) throws ServletException,
			IOException, SQLException {
		// Extract name of person to be added from request
		String firstName = request.getParameter("FirstName");
		String lastName = request.getParameter("LastName");

		// Add person if name is not null/empty
		if (firstName != null && lastName != null
				&& !firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
			Person person = new Person();
			person.setFirstName(firstName.trim());
			person.setLastName(lastName.trim());
			personDAO.addPerson(person);
		}
	}*/
	
	private void appendRecipeTable(HttpServletResponse response)
			throws SQLException, IOException {
		// Append table that lists all persons
		List<Recipe> resultList = recipeDAO.selectAllRecipes(connProductDAO);
		response.getWriter().println(
				"<p><table border=\"1\"><tr><th colspan=\"3\">"
						+ (resultList.isEmpty() ? "" : resultList.size() + " ")
						+ "Entries in the Database</th></tr>");
		if (resultList.isEmpty()) {
			response.getWriter().println(
					"<tr><td colspan=\"3\">Database is empty</td></tr>");
		} else {
			response.getWriter()
					.println(
							"<tr><th>Name</th><th>How to make</th><th>Id</th></tr>");
		}
		IXSSEncoder xssEncoder = XSSEncoder.getInstance();
		for (Recipe r : resultList) {
			response.getWriter().println(
					"<tr><td>" + xssEncoder.encodeHTML(r.getName())
							+ "</td><td>"
							+ xssEncoder.encodeHTML(r.getHowToMake())
							+ "</td><td>" + r.getId() + "</td>");
			response.getWriter().println(
					"<td><ul>");
			for(String prod: r.getProducts()){
				response.getWriter().println("<li>" + prod + "</li>");
			}
			response.getWriter().println("</ul></td></tr>");
		}
		response.getWriter().println("</table></p>");
	}
	
	private void addBeginingHtml(HttpServletResponse response) throws IOException {
		response.getWriter()
			.println("<!DOCTYPE html><html><head><title>"
					+ "</title></head><body>");
	}
	
	private void addEndingHtml(HttpServletResponse response) throws IOException {
		response.getWriter()
			.println("</body></html>");
	}
	
	private void addScriptsHtml(HttpServletResponse response) throws IOException {
		response.getWriter()
			.println("<script type=\"text/javascript\" src=\"./scripts/jquery-1.11.1.min.js\"></script>"
					+ "<script type=\"text/javascript\" src=\"./scripts/functions.js\"></script>");
	}
}