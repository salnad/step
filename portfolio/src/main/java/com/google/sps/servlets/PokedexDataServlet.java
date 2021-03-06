package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    HashMap<String, ArrayList<Integer>> stats = getStats();
    Gson gson = new Gson();
    String json = gson.toJson(stats);
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
    int entryNumColIndex = getColIndex(header, "#"); // Get Index for Pokemons Entry Number

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");
      // Retrieve "Entry Number" (id) of current Pokemon in row
      int pokedexEntryNum = Integer.valueOf(cells[entryNumColIndex]);
      // Loop through each field  to be sent
      for (String field : fields) {
        // Get the column number of the field in the CSV
        int fieldIndex = getColIndex(header, field);
        // Get value of that field in current row (for current pokemon)
        int fieldValue = Integer.valueOf(cells[fieldIndex]);
        // Retrieve the ArrayList mapped for current field in the result map
        // set the value of the current pokemon's stat indexing using the pokemons entry number (id)
        result.get(field).set(pokedexEntryNum, fieldValue);
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
