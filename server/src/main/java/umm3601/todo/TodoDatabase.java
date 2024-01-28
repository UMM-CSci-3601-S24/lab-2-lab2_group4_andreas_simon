package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

               if (queryParams.containsKey("limit")) {
            String limitParam = queryParams.get("limit").get(0);
            try {
                int limit = Integer.parseInt(limitParam);
                filteredTodos = limitTodos(filteredTodos, limit);
            } catch (NumberFormatException e) {
                throw new BadRequestResponse("Specified limit '" + limitParam + "' can't be parsed to an integer");
            }
        }

            

         /* if (queryParams.containsKey("age")) {
            String ageParam = queryParams.get("age").get(0);
            try {
              int targetAge = Integer.parseInt(ageParam);
              filteredTodos = filterTodosByAge(filteredTodos, targetAge);
            } catch (NumberFormatException e) {
              throw new BadRequestResponse("Specified age '" + ageParam + "' can't be parsed to an integer");
            }
          }
          // Filter company if defined
          if (queryParams.containsKey("status")) {
            String targetStatus = queryParams.get("status").get(0);
            filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
          } */


           //Filtering todos by status
        if (queryParams.containsKey("status")) {
            String statusParam = queryParams.get("status").get(0);
            filteredTodos = filterTodosByStatus(filteredTodos, statusParam);
        }

        if (queryParams.containsKey("contains")) {
            String containsParam = queryParams.get("contains").get(0);
            filteredTodos = filterTodosByContains(filteredTodos, containsParam);
        }

        if (queryParams.containsKey("owner")) {
            String ownerParam = queryParams.get("owner").get(0);
            filteredTodos = filterTodosByOwner(filteredTodos, ownerParam);
        }

        if (queryParams.containsKey("category")) {
            String categoryParam = queryParams.get("category").get(0);
            filteredTodos = filterTodosByCategory(filteredTodos, categoryParam);
        }

        return filteredTodos;
    }


    private Todo[] filterTodosByStatus(Todo[] todos, String statusParam) {
        boolean targetStatus = getStatusFromParam(statusParam);

        return Arrays.stream(todos)
            .filter(todo -> todo.status == targetStatus)
            .toArray(Todo[]::new);
    }

    // complete = true, incomplete = false
    private boolean getStatusFromParam(String statusParam) {
        switch (statusParam.toLowerCase()) {
            case "complete":
                return true;
            case "incomplete":
                return false;
            default:
                throw new BadRequestResponse("Invalid status parameter: " + statusParam);
        }
    }

      private Todo[] limitTodos(Todo[] todos, int limit) {
        return Arrays.stream(todos).limit(limit).toArray(Todo[]::new);
    }

    private Todo[] filterTodosByContains(Todo[] todos, String containsParam) {
        Pattern pattern = Pattern.compile(containsParam, Pattern.CASE_INSENSITIVE);

        return Arrays.stream(todos)
                .filter(todo -> pattern.matcher(todo.body).find())
                .toArray(Todo[]::new);
    }

    private Todo[] filterTodosByOwner(Todo[] todos, String ownerParam) {
        Pattern pattern = Pattern.compile(ownerParam, Pattern.CASE_INSENSITIVE);

        return Arrays.stream(todos)
                .filter(todo -> pattern.matcher(todo.owner).find())
                .toArray(Todo[]::new);
    }

    private Todo[] filterTodosByCategory(Todo[] todos, String categoryParam) {
        Pattern pattern = Pattern.compile(categoryParam, Pattern.CASE_INSENSITIVE);

        return Arrays.stream(todos)
                .filter(todo -> pattern.matcher(todo.category).find())
                .toArray(Todo[]::new);
    }

    
}
