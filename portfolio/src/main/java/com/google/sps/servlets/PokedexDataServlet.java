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
  private static final int NUMBER_OF_GENERATIONS =
      7; // 6 generations, Array Index directly  (1st Gen -> index of 1)
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    int[] generations = getGenerationCount();
    Gson gson = new Gson();
    String json = gson.toJson(generations);
    response.getWriter().println(json);
  }

  private int[] getGenerationCount() {
    int[] result = new int[NUMBER_OF_GENERATIONS];
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/pokemon.csv"));
    String header = scanner.nextLine();
    int generationColIndex = getColIndex(header, "Generation");
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      result[Integer.valueOf(cells[generationColIndex])]++;
    }
    scanner.close();
    return result;
  }

  private int getColIndex(String header, String colName) {
    String[] colNames = header.split(",");
    for (int i = 0; i < colNames.length; i++) {
      if (colName.equals(colNames[i])) {
        return i;
      }
    }
    System.error.println("ERROR: Could not find column " + colName + "in header " + header);
    return -1;
  }
}
