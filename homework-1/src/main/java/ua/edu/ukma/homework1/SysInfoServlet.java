package ua.edu.ukma.homework1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@WebServlet(name = "sysInfoServlet", value = "/sysinfo")
public class SysInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        String osName = osBean.getName();
        int cpuCores = osBean.getAvailableProcessors();

        long totalRamGb = osBean.getTotalMemorySize() / (1024 * 1024 * 1024);
        long freeRamGb = osBean.getFreeMemorySize() / (1024 * 1024 * 1024);

        out.println("<html><body>");
        out.println("<h2>Характеристики сервера:</h2>");
        out.println("<ul>");
        out.println("<li><b>Операційна система:</b> " + osName + "</li>");
        out.println("<li><b>Кількість ядер CPU:</b> " + cpuCores + "</li>");
        out.println("<li><b>Загальна RAM:</b> " + totalRamGb + " ГБ</li>");
        out.println("<li><b>Вільна RAM:</b> " + freeRamGb + " ГБ</li>");
        out.println("</ul>");
        out.println("</body></html>");
    }
}
