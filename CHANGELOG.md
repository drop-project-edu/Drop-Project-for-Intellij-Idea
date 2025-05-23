<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Drop Project Plugin Changelog

## [Unreleased]

## [0.9.12] - 2025-05-18

- When adding two different assignments, the second assignment was being identified as duplicate of the first one

## [0.9.11] - 2025-05-18

- The plugin doesn't overwrite the AUTHORS.txt file anymore
- The build report now shows the student unit tests results (issue #10)
- The plugin is now compatible with Intellij IDEA 2025.1

## [0.9.10] - 2025-02-16

- Fixed an issue where the plugin was always showing the first build report instead of the last (issue #8)

## [0.9.9] - 2025-01-28

- Improved handling of server errors during submissions, including scenarios like “aborted by timeout”
- Automatically include README.txt or README.md files in submissions, if present.
- Redesigned the assignment instructions UI to align seamlessly with the current theme.
- Fixed an issue where images were not displayed in the assignment instructions; they now render correctly.

## [0.9.8] - 2024-11-22

- Minor improvements
- Shows a custom message when the token is expired
- The plugin is now compatible with Intellij IDEA 2024.3

## [0.9.7-beta1] - 2024-08-19

### Changed

- Improved the submission process, it now properly runs in the background, with a cancellable status bar
showing progress

### Fixed

- Fixed an error when there wasn't any selected assignment

## [0.9.6] - 2024-08-16

### Changed

- The plugin is now compatible with Intellij IDEA 2024.2 and requires at least 2023.3
- The server is now a drop down and users have to select one on their first login

## [0.9.5] - 2024-08-09

### Changed

- Updated build configuration

## [0.9.4] - 2024-01-04

### Added

- Copy build report errors feature
- Open in web build report feature

[Unreleased]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.12...HEAD
[0.9.12]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.11...v0.9.12
[0.9.11]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.10...v0.9.11
[0.9.10]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.9...v0.9.10
[0.9.9]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.8...v0.9.9
[0.9.8]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.7-beta1...v0.9.8
[0.9.7-beta1]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.6...v0.9.7-beta1
[0.9.6]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.5...v0.9.6
[0.9.5]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/compare/v0.9.4...v0.9.5
[0.9.4]: https://github.com/drop-project-edu/Drop-Project-for-Intellij-Idea/commits/v0.9.4
