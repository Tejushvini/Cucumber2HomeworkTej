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
      | firstname | lastname | email               | fpassword  | confirmpassword | group             |
      | John      | Doe      | john103@gmail.com   | @123456789 | @123456789      | Group T (2026)    |

  # Approval / post-registration actions moved into a separate scenario
  Scenario Outline: Approve the new user (admin flow)
    Given the admin is on the login page
    And admin user logs in as admin <adminemail> with password <adminpassword>
    And click on admin button
    And click on admin panel
    And enter email <email> in the search box
    And verify that the new register user is displayed in the search results
    And verify if the account status for the new user is "Inactive"
    And on inactive status to trigger activation popup
    And click ok button on the activation popup

    Examples:
      | adminemail         | adminpassword | email             |
      | admin@gmail.com  | @12345678  | john103@gmail.com |

  # Note: The approval steps must have matching step definitions in Java (stepDefinitions/Registration.java or another step file).
  # If those step definitions are not present, implement them or remove/replace these steps with ones that exist.
