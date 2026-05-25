# Microservices Platform

## Overview

This repository contains a distributed microservices platform built with Spring Boot, gRPC, Apache Kafka, and AWS cloud services.

The system exposes HTTP REST APIs through a Spring Boot gateway/controller application, while backend service-to-service communication is handled using gRPC.

All production services are containerized and deployed on AWS Fargate for scalability and simplified infrastructure management.

# System Architecture

## Core Technologies

* Java
* Spring Boot
* gRPC
* Apache Kafka
* AWS ECS Fargate
* AWS MSK
* AWS DocumentDB
* Docker
* Maven

# Architecture Overview

## API Layer

This application manages customers along with their addresses.



The backend consists of independently deployable Spring Boot microservices.

Services communicate internally using gRPC for low-latency and strongly typed communication.

Example services include:

| Service              | Responsibility                            |
| -------------------- | ----------------------------------------- |
| Customer Service     | Manages customer information              |
| Notification Service | Handles email and notification processing |
| Order Service        | Handles order workflows                   |
| Payment Service      | Processes payments                        |
| Shipping Service     | Manages shipment processing               |

---

# Data Storage

## AWS DocumentDB

* Customer information is stored in AWS DocumentDB.

# Local Development

## Prerequisites

Install the following:

* SpringBoot 4.0.1
* Java 21+
* Maven
* Docker


