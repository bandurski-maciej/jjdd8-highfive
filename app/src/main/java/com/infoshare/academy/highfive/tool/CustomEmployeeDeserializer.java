package com.infoshare.academy.highfive.tool;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.infoshare.academy.highfive.employeemanager.Employee;
import com.infoshare.academy.highfive.employeemanager.EmployeeManagementSingleton;
import com.infoshare.academy.highfive.employeemanager.Team;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomEmployeeDeserializer extends StdDeserializer<Employee> {
    private static final List<Team> teamList = EmployeeManagementSingleton.getInstance().getTeamList();

    public CustomEmployeeDeserializer() {
        this(null);
    }

    private CustomEmployeeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Employee deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String employeeId = node.get("employee_id").asText();

        String firstName = node.get("first_name").asText();

        String surname = node.get("surname").asText();

        LocalDate hireDate = LocalDate.parse(node.get("hire_date").asText(), DateTimeFormatter.ISO_DATE);

        String holidayEntitlement = node.get("holiday_entitlement").asText();

        Integer additionalEntitlement = (node.get("additional_entitlement").asText().equals("null") ? 0 : Integer.parseInt(node.get("additional_entitlement").asText()));

        Integer teamId = (node.get("team").get("team_id").asText().equals("null") ? 0 : Integer.parseInt(node.get("team").get("team_id").asText()));

        Team teamName = teamList.stream().filter(i -> i.getTeamId().equals(teamId)).findFirst().orElse(teamList.get(0));

        return new Employee(Integer.parseInt(employeeId), firstName, surname, hireDate, Integer.parseInt(holidayEntitlement), additionalEntitlement, teamName);
    }
}
