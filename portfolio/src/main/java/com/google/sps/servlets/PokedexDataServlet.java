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
  private static final int NUMBER_OF_POKEMON = 800;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    int[] generations = getGenerationCount();
    Gson gson = new Gson();
    String json = gson.toJson(generations);
    response.getWriter().println(json);
  }

  private HashMap<String, ArrayList<Integer>> getStats() {
    HashMap<String, ArrayList<Integer>> result = new HashMap<String, ArrayList<Integer>>();
    String[] fields = {"HP", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed"};

    for (String field : fields) {
      result.put(field, new ArrayList<Integer>(Collections.nCopies(NUMBER_OF_POKEMON, 0)));
    }

    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/pokemon.csv"));
    String header = scanner.nextLine();
    int entryNumColIndex = getColIndex(header, "#");

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      int pokedexEntryNum = Integer.valueOf(cells[entryNumColIndex]);

      for (String field : fields) {
        int fieldIndex = getColIndex(header, field);
        result.get(field).set(pokedexEntryNum, Integer.valueOf(cells[fieldIndex]));
      }
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
    System.err.println("ERROR: Could not find column " + colName + "in header " + header);
    return -1;
  }
}
