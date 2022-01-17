# Valuation 

## Problem
If all calculation requests will be sent to Grid Computing from orchestrator, fast requests (e.g. calculation time within 1 minute) may be blocked by slow requests (e.g. calculation time in hours).


## Solution
I can introduce some strategy class to differentiate between fast and slow requests first.
- In this code, I create an over-simplified ProductFastSlowExecutionStrategy class, which differentiate fast and slow requests by product type.

By injecting strategy object into Orchestrator, it can use the strategy to classify the requests: 
- If the requests are identified as fast, send it to a separate message queue, and it can be picked up some engine(s) outsides Grid Computing.
- Instead, if the requests are identified as slow, keep it to be processed by Grid Computing.


## Components

### Orchestrator
This component provides the service (single-threaded request processor) to:
- read incoming valuation request from the designated message queue
- send command to target queue based on the provided execution strategy
 
### Engine Manager
This component provides the service (multi-threaded engines) to:
- read command from the designated message queue
- perform calculation
- send to result queue which client can poll/be notified for the results

## Architecture
Event-Driven architecture
- Since valuation calculation can take long time to perform, I should not make use of synchronous communication
- It is flexible: I can code different components (client, orchestrator, engine manager) independent of each other 

## Application Entry Point
ValuationTestApp class, main method

Purpose: 
This test app is used to send a batch of fast and slow valuation requests, and measure the elapsed time for:
- Default Strategy: both fast and slow requests queued in a single message queue
- Fast Slow Strategy: fast and slow requests queued in separate message queues


## Design Patterns Used

### Strategy Pattern
Classes: DefaultExecutionStrategy, ProductFastSlowExecutionStrategy

Reasons: 
- They encapsulate the algorithms in a class 
- They allow the consumer to specify the execution strategy at runtime 


### Command Pattern
Classes: ValuationCommand

Reasons: 
- It encapsulates all information for performing an action by our engine


### Factory Pattern
Classes: ExecutionStrategyFactory

Reasons: 
- This factory class encapsulate the exact subclass (strategy implementation) to be created


### Singleton Pattern
Classes: ValuationLogger

Reasons:
- ValuationLogger can be shared by all objects for logging to stdout
- I make it Singleton to avoid each object instantiate its own logger instance which is waste of heap memory.






