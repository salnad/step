// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private Entity createComment(String content) {
    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("content", content);
    commentEntity.setProperty("timestamp", timestamp);
    return commentEntity;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String content = request.getParameter("text-input");
    if (content.isEmpty() || content == null) {
      response.sendRedirect("/walkthrough/");
      return;
    }

    Entity newComment = createComment(content);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newComment);

    response.sendRedirect("/walkthrough/");
  }

  private int getCommentLimit(String commentLimitString) {
    int commentLimit;
    try {
      commentLimit = Integer.parseInt(commentLimitString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + commentLimitString);
      return -1;
    }
    if (commentLimit < 0) {
      System.err.println("Comment limit is out of range: " + commentLimitString);
      return -1;
    }
    return commentLimit;
  }

  private List<String> getComments(int commentLimit) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<String>();

    int commentsAdded = 0;
    for (Entity entity : results.asIterable()) {
      if (commentsAdded >= commentLimit) {
        break;
      }
      String content = (String) entity.getProperty("content");
      comments.add(content);
      commentsAdded++;
    }
    return comments;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentLimitString = request.getParameter("comment_limit");
    int commentLimit = getCommentLimit(commentLimitString);

    response.setContentType("application/json;");
    List<String> comments = getComments(commentLimit);

    Gson gson = new Gson();
    String jsonData = gson.toJson(comments);
    response.getWriter().println(jsonData);
  }
}
