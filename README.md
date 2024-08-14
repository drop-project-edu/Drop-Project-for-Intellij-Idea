# Drop Project Plugin for Intellij Idea

![[Static Badge](https://img.shields.io/badge/version-v0.9.4-blue)](https://img.shields.io/badge/version-v0.9.6-blue)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/21870-drop-project)](https://img.shields.io/jetbrains/plugin/d/25078-drop-project)

<!-- Plugin description -->
[Drop Project](https://dropproject.org) is an open-source automated assessment tool that checks student programming projects for correctness and
quality. Currently, it supports Java and Kotlin projects.

This plugin allows students to submit their projects directly from Intellij IDEA. They can also review the submission build results.
<!-- Plugin description end -->

## Features

    Add assignments
    Check assignments details
    Submit assignments
    Review assignments results

## Requirements

* IntelliJ IDEA installed
* Access to Drop Project Website

## How to get it

* Access the IDE settings in File > Settings, or access directly through the settings icon in the upper right corner
* Go to the Plugins section
* Go to the Marketplace section in plugins and search for Drop Project
* Select Install

## How to use

* This plugin is mainly concentrated in a toolwindow, which is probably in the right panel of your IDE
* Login with your credentials (Your name; Your Drop Project Token)
* First icon in the toolbar is to add an assignment
* Second icon is to submit your code
* Third icon is to refresh the assignment list
* The last icon is to log out
* If you submit an assignment, a forth icon will appear which is used to check the buid report of your last submission
* You can access some additional plugin settings in the settings icon on the toolwindow top title

## How to create a new version

* Make sure CHANGELOG.md includes all the changes of the version to be created, under the unreleased. Don't create the version itself, it will be created automatically
* Change the `pluginVersion` property on `gradle.properties`
* Update, if necessary, the `pluginUntilBuild` property
    * In this case, make sure to test the plugin in that Intellij version. Change the platformVersion property and execute `Run Plugin`.
* Execute `Run Verifications`
* Push to github. This will create a draft release.
* Publish that release - this will trigger a github action that will publish the plugin to the marketplace.