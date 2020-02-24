package com.infoshare.academy.highfive.web.servlet.holiday;


import com.infoshare.academy.highfive.dto.view.HolidayView;
import com.infoshare.academy.highfive.freemarker.TemplateProvider;
import com.infoshare.academy.highfive.service.HolidayService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/employee/search-by-name")
public class SearchHolidayByNameServlet extends HttpServlet {

    Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    @Inject
    private TemplateProvider templateProvider;

    @Inject
    HolidayService holidayService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

      PrintWriter writer = resp.getWriter();
      HttpSession session = req.getSession();
      Map<String, Object> dataModel = new HashMap<>();

      Template template = this.templateProvider.getTemplate(getServletContext(), "template.ftlh");

      dataModel.put("method", req.getMethod());
      dataModel.put("contentTemplate", "holiday-search.ftlh");
      dataModel.put("title", "Search result by name");
      dataModel.put("pluginCssTemplate", "plugin-css-stylesheet.ftlh");
      dataModel.put("pluginJsTemplate", "plugin-js-servlets.ftlh");

      dataModel.put("loggedEmployee", session.getAttribute("loggedEmployee"));
      dataModel.put("loggedEmployeeRole", session.getAttribute("loggedEmployeeRole"));

      try {
        template.process(dataModel, writer);
      } catch (
        TemplateException e) {
        LOGGER.warn("Issue with processing Freemarker template.{}", e.getMessage());
      }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String searchByName = req.getParameter("search_by_name");

        List<HolidayView> holidays = null;
        boolean validInputs = false;

        if (searchByName.length() > 2) {
            validInputs = true;
            LOGGER.info("Inputs Correct. Processing!");
            holidays = holidayService.searchHolidayByName(searchByName);

        }

        Template template = this.templateProvider.getTemplate(getServletContext(), "template.ftlh");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        Map<String, Object> dataModel = new HashMap<>();

        dataModel.put("method", req.getMethod());
        dataModel.put("contentTemplate", "holiday-search-result.ftlh");
        dataModel.put("title", "Search result by name");
        dataModel.put("pluginCssTemplate", "plugin-css-all-holiday.ftlh");
        dataModel.put("pluginJsTemplate", "plugin-js-all-holiday.ftlh");
        dataModel.put("validInputs", validInputs);
        dataModel.put("searchType", "by name");
        dataModel.put("holidays", holidays);

        dataModel.put("loggedEmployee", session.getAttribute("loggedEmployee") );
        dataModel.put("loggedEmployeeRole",session.getAttribute("loggedEmployeeRole") );


        try {
            template.process(dataModel, writer);
        } catch (
                TemplateException e) {
            LOGGER.warn("Issue with processing Freemarker template.{}", e.getMessage());
        }

    }
}
