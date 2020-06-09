package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/pokedex-data")
public class PokedexDataServlet extends HttpServlet {

  private int[] getGenerationCount() {
    int[] result = new int[10];
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/pokemon.csv"));
    String header = scanner.nextLine();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      result[Integer.valueOf(cells[11])]++;
    }
    scanner.close();
    return result;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    int[] generations = getGenerationCount();
    Gson gson = new Gson();
    String json = gson.toJson(generations);
    response.getWriter().println(json);
  }
}
