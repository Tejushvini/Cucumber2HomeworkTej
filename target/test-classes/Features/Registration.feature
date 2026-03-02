@Registration
Feature: User Registration
  As an admin I want to register a new user

  Scenario Outline: Successful registration of new user with valid details
    Given the admin is on the login page
    When the admin click the sign up link
    Then the admin is on the registration page
    When the admin enters valid firstname <firstname>
    And the admin enters valid lastname <lastname>
    And the admin enters valid email AUTO_GENERATE
    And the admin enter valid first password <fpassword>
    And the admin enter valid confirm password <confirmpassword>
    And the confirm password is the same as the password
    And the admin selects the correct group <group>
    When the admin click the create account button
    Then the successful registration message is displayed

    Examples:
      | firstname | lastname | fpassword  | confirmpassword | group             |
      | Tej      | Doe      | @123456789 | @123456789      | Group T (2026)    |

  # Approval / post-registration actions moved into a separate scenario
  Scenario: Approve the new user (admin flow) - Search by Generated Email
    Given the admin is on the login page
    And admin user logs in as admin admin@gmail.com with password @12345678
    And click on admin button
    And click on admin panel
    And click on Approvals button
    And the admin searches for the registered user by generated email
    And verify the generated email appears in search results
    And verify if the account status for the new user is "Inactive"
    And on inactive status to trigger activation popup
    And click ok button on the activation popup

