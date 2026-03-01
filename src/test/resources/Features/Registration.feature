@Registration
Feature: User Registration
  As an admin I want to register a new user

  Scenario Outline: Successful registration of new user with valid details
    Given the admin is on the login page
    When the admin click the sign up link
    Then the admin is on the registration page
    When the admin enters valid firstname <firstname>
    And the admin enters valid lastname <lastname>
    And the admin enters valid email <email>
    And the admin enter valid first password <fpassword>
    And the admin enter valid confirm password <confirmpassword>
    And the confirm password is the same as the password
    And the admin selects the correct group <group>
    When the admin click the create account button
    Then the successful registration message is displayed
    Examples:
      | firstname | lastname | email | fpassword | confirmpassword | group |
      | John | Doe | john103@gmail.com | @123456789 | @123456789 | Group T (2026) |