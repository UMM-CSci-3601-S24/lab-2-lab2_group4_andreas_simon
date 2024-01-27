package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

public class TodoDatabase {

    private Todo[] allTodos;

    public TodoDatabase(String todoDataFile) throws IOException{
        InputStream resourceAsStream = getClass().getResourceAsStream(todoDataFile);
        if (resourceAsStream == null) {
            throw new IOException("Could not find " + todoDataFile);
        }
        InputStreamReader reader = new InputStreamReader(resourceAsStream);

        ObjectMapper objectMapper = new ObjectMapper();

        allTodos = objectMapper.readValue(reader, Todo[].class);
    }

    public int size() {
        return allTodos.length;
    }

    public Todo getTodo(String id) {
        return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
    }

    public Todo[] listTodos(Map<String, List<String>> queryParams) {
        Todo[] filteredTodos = allTodos;
        //add query parameters here

          if (queryParams.containsKey("id")) {
            String targetId = queryParams.get("id").get(0);
            filteredTodos = filterTodosById(filteredTodos, targetId);
          }

          if (queryParams.containsKey("owner")) {
            String targetOwner = queryParams.get("owner").get(0);
            filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
          }

          if (queryParams.containsKey("status")) {
            String statusParam = queryParams.get("status").get(0);
            try {
              boolean targetStatus = Boolean.parseBoolean(statusParam);
              filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
            } catch (NumberFormatException e) {
              throw new BadRequestResponse("Specified status '" + statusParam + "' can't be parsed to a boolean");
            }
          }

          if (queryParams.containsKey("body")) {
            String targetBody = queryParams.get("body").get(0);
            filteredTodos = filterTodosByBody(filteredTodos, targetBody);
          }

          if (queryParams.containsKey("category")) {
            String targetCategory = queryParams.get("category").get(0);
            filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
          }

        return filteredTodos;
    }

    public Todo[] filterTodosById (Todo[] todos, String targetId) {
      return Arrays.stream(todos).filter(x -> x._id.equals(targetId)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByOwner (Todo[] todos, String targetOwner) {
      return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByStatus(Todo[] todos, boolean targetStatus) {
      return Arrays.stream(todos).filter(x -> x.status == (targetStatus ? 1 : 0)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByBody(Todo[] todos, String targetBody) {
      return Arrays.stream(todos).filter(x-> x.body.equals(targetBody)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory) {
      return Arrays.stream(todos).filter(x-> x.body.equals(targetCategory)).toArray(Todo[]::new);
    }
}
