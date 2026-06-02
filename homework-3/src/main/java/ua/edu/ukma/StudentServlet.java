package ua.edu.ukma;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet(name = "studentServlet", value = "/students")
public class StudentServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Вміст файлу students.csv:</h2>");
        out.println("<table border='1' cellpadding='5'>");
        out.println("<tr><th>Ім'я студента</th><th>Бал</th></tr>");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("students.csv")) {
            if (is == null) {
                out.println("</table><p style='color:red;'>Помилка: Файл students.csv не знайдено у зібраному архіві!</p>");
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                boolean isFirstLine = true;
                
                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        out.println("<tr><td>" + parts[0] + "</td><td>" + parts[1] + "</td></tr>");
                    }
                }
            }
        } catch (Exception e) {
            out.println("<p>Системна помилка читання: " + e.getMessage() + "</p>");
        }

        out.println("</table>");
        out.println("<br/><a href='index.jsp'>Повернутися на головну</a>");
        out.println("</body></html>");
    }
}