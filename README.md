# SpectraTest

SpectraTest is a no-code UI test automation platform designed to execute automated UI tests across web, mobile and desktop applications using a distributed control–agent architecture.

This public repository showcases the core backend orchestration and locator-based automation workflow of the SpectraTest platform.

---

## 🚀 Overview

SpectraTest aims to simplify UI test automation by allowing test execution to be centrally controlled while delegating UI interactions to distributed agent services.

The system is designed to support:
- Scalable test execution
- Multiple automation agents
- Extensible architecture for future enhancements

---

## 🧠 Architecture

SpectraTest follows a **control–agent architecture**:

- **Control Service**
  - Implemented using **Java and Spring Boot**
  - Orchestrates test execution
  - Manages test lifecycle and coordination
  - Communicates with agents via **RabbitMQ**

- **Test Automation Agents**
  - Implemented as independent Spring Boot services
  - Execute UI interactions based on received instructions
  - Perform locator-based actions such as click, input, and validation
  - Utilize:
    - Selenium for web automation
    - Appium for mobile automation
    - Appium and LDTP for desktop automation

Communication between the control service and agents is asynchronous and message-driven.

---

## 🧩 Core Components

- **Test Execution Orchestrator**
  - Receives test definitions
  - Coordinates execution flow
  - Dispatches commands to agents

- **Message Queue (RabbitMQ)**
  - Handles communication between control and agents
  - Enables decoupled and scalable execution

- **Automation Agents**
  - Perform UI interactions based on received instructions
  - Execute locator-based actions such as click, input, and validation

---

## 🛠 Tech Stack

- **Backend:** Java, Spring Boot (Control and Agent Services)
- **Frontend:** TypeScript, Next.js
- **Messaging:** RabbitMQ
- **Automation:** Selenium, Appium, LDTP
- **Build Tool:** Maven
- **Version Control:** Git

---

## 🔄 Example Execution Flow

Below is a simplified execution flow illustrating how a UI test scenario is executed within SpectraTest:

1. **Test Trigger (Control Service)**
   - The control service receives a test execution request (e.g., via API or predefined configuration).
   - The test definition includes the sequence of UI actions and target platform information.

2. **Message Dispatch (RabbitMQ)**
   - The control service publishes execution commands to RabbitMQ.
   - Messages contain action details such as locator type, locator value, and action parameters.

3. **Agent Execution (Agent Service)**
   - A suitable Spring Boot–based agent service consumes the message from the queue.
   - The agent performs the requested UI interaction using:
     - Selenium (for web applications), or
     - Appium (for mobile applications).
   - Execution results are processed locally by the agent.

This message-driven flow enables loose coupling between the control service and agent services, allowing multiple agents to operate independently and scale horizontally.

---

## 📌 Public Version Scope

This repository represents the **core, public-facing version** of SpectraTest and focuses on:

- Control service implementation
- Distributed execution model
- Locator-based UI automation flow

Advanced features such as visual-based fallback mechanisms and experimental modules are developed separately and are not included in this public version.

---

## 🎯 Motivation

SpectraTest was developed to explore and address challenges in UI test automation, including:
- Tight coupling between test logic and UI implementations
- Limited scalability of monolithic test execution setups
- The need for extensible and distributed automation infrastructures

---

## 📈 Future Work

Planned improvements include:
- Enhanced agent management
- Visual-based UI element recognition as a fallback mechanism
- Improved reporting and result aggregation
- Support for additional platforms

---

## 📄 License

This project is provided for educational and demonstration purposes.
