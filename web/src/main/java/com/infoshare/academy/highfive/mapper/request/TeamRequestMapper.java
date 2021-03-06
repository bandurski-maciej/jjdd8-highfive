package com.infoshare.academy.highfive.mapper.request;


import com.infoshare.academy.highfive.dto.request.TeamRequest;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class TeamRequestMapper {

  public TeamRequest mapParamsToRequest(HttpServletRequest request) {

    TeamRequest teamRequest = new TeamRequest();

    teamRequest.setTeamName((String) request.getAttribute("team_name"));

    return teamRequest;
  }
}
