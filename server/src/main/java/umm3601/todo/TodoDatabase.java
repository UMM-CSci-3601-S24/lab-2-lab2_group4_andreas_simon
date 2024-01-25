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

    /*     if (queryParams.containsKey("age")) {
            String ageParam = queryParams.get("age").get(0);
            try {
              int targetAge = Integer.parseInt(ageParam);
              filteredTodos = filterTodosByAge(filteredTodos, targetAge);
            } catch (NumberFormatException e) {
              throw new BadRequestResponse("Specified age '" + ageParam + "' can't be parsed to an integer");
            }
          }
          // Filter company if defined
          if (queryParams.containsKey("company")) {
            String targetCompany = queryParams.get("company").get(0);
            filteredTodos = filterTodosByCompany(filteredTodos, targetCompany);
          } */
        
        return filteredTodos;
    }


}
