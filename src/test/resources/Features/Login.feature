Feature: User login test

  Scenario Outline: Successful login with valid credentials
    Given I am on the login page
    When I enter valid username <username>
    And I enter valid password <password>
    And I click the login button
    Then I should be redirected to the dashboard

    Examples:
      | username          | password  |
      | tej1@gmail.com | @123456789 |