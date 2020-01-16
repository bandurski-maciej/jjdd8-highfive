package com.infoshare.academy.highfive.servlet;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet
public class SearchHolidayByNameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String inputSearch = req.getParameter("inputSearch");

        String reqUser = "*" + inputSearch + "*";

        super.doPost(req, resp);
    }
}
