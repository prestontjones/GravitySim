# GravityGame

A physics-based sandbox simulator built with [libGDX](https://libgdx.com/), where you can create and interact with celestial bodies in a gravity-driven environment.

## Overview

GravityGame is a sandbox physics simulator that allows you to create and manipulate planets, stars, and other celestial bodies in a dynamic gravitational environment. The game features a minimalist wireframe aesthetic with a focus on realistic physics simulation and intuitive user interaction.

## Features

### Physics Simulation
- Realistic gravitational interactions between all bodies
- Scalable to handle as many physics bodies as your system can support
- Adjustable body properties including size, mass, and velocity
- Toggle collisions between bodies

### Visualization
- Path prediction showing future trajectories of all bodies
- Velocity indicators displaying direction and magnitude
- Multiple rendering modes (wireframe and solid)
- Parallax star background providing visual feedback on camera movement

### User Interface
- Intuitive camera controls (zoom, pan)
- Body creation workflow with visual feedback
- Simulation controls (start, pause)

## Platform Support

- `core`: Main module with the application logic shared by all platforms
- `lwjgl3`: Primary desktop platform using LWJGL3

## Development

GravityGame is developed using:
- Java with libGDX framework
- Clean, structured, and maintainable code
- Dynamic screen resizing for any display
- Best coding practices for maintainability

## Building and Running

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper is included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.

### Useful Gradle Tasks

- `lwjgl3:run`: Starts the application
- `lwjgl3:jar`: Builds application's runnable jar (found at `lwjgl3/build/libs`)
- `build`: Builds sources and archives of every project
- `clean`: Removes `build` folders containing compiled classes and built archives
- `test`: Runs unit tests

### Project-Specific Tasks

Most tasks can be run with a `name:` prefix for specific projects:
- `core:clean`: Removes `build` folder only from the `core` project

### IDE Support

- `eclipse`: Generates Eclipse project data
- `idea`: Generates IntelliJ project data
- `cleanEclipse`: Removes Eclipse project data
- `cleanIdea`: Removes IntelliJ project data

## Gradle Flags

- `--continue`: Errors will not stop tasks from running
- `--daemon`: Uses Gradle daemon to run chosen tasks
- `--offline`: Uses cached dependency archives
- `--refresh-dependencies`: Forces validation of all dependencies (useful for snapshot versions)
