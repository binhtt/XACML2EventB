# XACML2Event-B

This repository provides the supporting artifact for the article  
**"Xacml2Event-B: a formal approach for verifying XACML access control policies using Event-B"**.

It contains the Java implementation of the XACML-to-Event-B transformation tool, the case-study policy data, the Event-B model used for formal verification, experimental results, and documentation for reproducing the transformation and verification workflow.

## Repository Structure

```text
XACML2EventB/
├── src/
│   ├── module-info.java
│   └── xacml2evtb/
│       └── Xacml2EventBTool.java
│
├── models/
│   └── FMSEventB/
│       ├── XACML_Context.ctx
│       ├── XACML_Refined1_Context.ctx
│       ├── XACML_AbsMachine.mch
│       ├── XACML_Refined1_Machine.mch
│       └── XACML_Refined2_Machine.mch
│
├── examples/
│   ├── HACS/
│   ├── FMS/
│   └── SIS/
│
├── results/
│   ├── transformation/
│   └── verification/
│
├── docs/
│   ├── installation.md
│   ├── usage.md
│   └── reproducibility.md
│
└── README.md
```

## XACML2Event-B Tool

The Java implementation is available under `src/`.

The tool provides three main functions:

- **Xacml2EvtB** — transforms supported XACML policy structures into Event-B contexts and machines.
- **Txt2Xacml** — converts structured textual rule descriptions into XACML.
- **Xacml2Txt** — extracts rule information from XACML into a textual representation.

The XACML-to-Event-B transformation generates the following components:

```text
XACML_Context.ctx
XACML_Refined1_Context.ctx
XACML_AbsMachine.mch
XACML_Refined1_Machine.mch
XACML_Refined2_Machine.mch
```

The generated components follow the successive modeling and refinement structure described in the article.

### Supported XACML Constructs

The current implementation supports the XACML constructs considered by the framework, including:

- PolicySet
- Policy
- Rule
- Permit and Deny rule effects
- PermitOverrides
- DenyOverrides
- FirstApplicable

The Event-B decision domain additionally includes `NotApplicable` as an evaluation outcome.

## Case-Study Data

The `examples/` directory contains policy data for the three software systems evaluated in the article:

- **HACS** — Healthcare Access Control System
- **FMS** — Financial Management System
- **SIS** — Student Information System

The datasets contain the rule and policy structures used to evaluate the transformation capabilities of XACML2Event-B.

## Event-B Model

The `models/FMSEventB/` directory contains the Event-B model used for formal verification.

The model consists of:

- `XACML_Context.ctx` — static XACML concepts, rule effects, and decision domain.
- `XACML_Refined1_Context.ctx` — policy hierarchy and combining-algorithm definitions.
- `XACML_AbsMachine.mch` — abstract request evaluation and rule applicability.
- `XACML_Refined1_Machine.mch` — refinement introducing policy applicability and combining semantics.
- `XACML_Refined2_Machine.mch` — refinement containing the policy decision correctness properties verified through proof obligations.

The model can be inspected and verified using the Rodin Platform.

## Requirements

- JDK 17 or later
- Rodin Platform
- Eclipse IDE for Java Developers (optional)

The Java implementation uses standard Java libraries and does not require additional third-party Java dependencies.

## Running the Tool

The main Java class is:

```text
src/xacml2evtb/Xacml2EventBTool.java
```

In Eclipse:

1. Create or import a Java project using JDK 17 or later.
2. Add the contents of `src/` to the project.
3. Open `Xacml2EventBTool.java`.
4. Select **Run As → Java Application**.
5. Select the `Xacml2EvtB` tab.
6. Load an XACML policy and select **Convert -> Event-B**.
7. Use **Save all** to save the generated Event-B components.

Detailed instructions are provided in [`docs/installation.md`](docs/installation.md) and [`docs/usage.md`](docs/usage.md).

## Reproducing the Verification

The general verification workflow is:

```text
XACML policy
     |
     v
XACML2Event-B transformation
     |
     v
Event-B contexts and machines
     |
     v
Rodin proof-obligation generation
     |
     v
Automatic / interactive proof
     |
     v
Verification results
```

To inspect the verified Event-B model:

1. Install the Rodin Platform.
2. Open or import the model under `models/FMSEventB/`.
3. Open the contexts and machines in refinement order.
4. Generate the proof obligations.
5. Apply the Rodin automatic provers.
6. Inspect the remaining proof obligations requiring interactive reasoning.
7. Compare the results with those reported under `results/`.

A detailed reproduction workflow is provided in [`docs/reproducibility.md`](docs/reproducibility.md).

## Experimental Results

The `results/` directory contains supporting results for:

- XACML-to-Event-B transformation experiments.
- Event-B verification and proof-obligation analysis.

These materials correspond to the experimental evaluation reported in the article.

## Documentation

Additional documentation is available under `docs/`:

- [`installation.md`](docs/installation.md) — installation and execution instructions.
- [`usage.md`](docs/usage.md) — tool functions and usage workflow.
- [`reproducibility.md`](docs/reproducibility.md) — steps for reproducing the transformation and formal verification workflow.

## License

The source code and supporting materials are provided for research and reproducibility purposes.
