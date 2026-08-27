# Reproducibility

This document describes the workflow for reproducing the XACML2Event-B
transformation and verification experiments.

## Repository Contents

The repository is organized as follows:

    src/       Java source code of the XACML2Event-B tool
    examples/  XACML policy data for the case studies
    models/    Event-B models used for formal verification
    results/   Reported transformation and verification results
    docs/      Installation, usage, and reproducibility documentation

## Case Studies

The evaluation considers three software systems:

- Healthcare Access Control System (HACS)
- Financial Management System (FMS)
- Student Information System (SIS)

The corresponding policy data are provided in the `examples/` directory.

## Reproducing the Transformation

1. Install JDK 17 or later.
2. Build and run `Xacml2EventBTool.java` as described in
   `docs/installation.md`.
3. Open the `Xacml2EvtB` tab.
4. Load the selected XACML policy data.
5. Select **Convert -> Event-B**.
6. Inspect the generated Event-B contexts and machines.
7. Use **Save all** to store the generated components.

The generated model consists of:

    XACML_Context
    XACML_Refined1_Context
    XACML_AbsMachine
    XACML_Refined1_Machine
    XACML_Refined2_Machine

## Reproducing Event-B Verification

1. Install the Rodin Platform.
2. Open or import the Event-B model provided under `models/FMSEventB/`.
3. Open the contexts and machines in their refinement order.
4. Generate the proof obligations in Rodin.
5. Apply the available automatic provers.
6. Inspect any remaining proof obligations requiring interactive reasoning.
7. Compare the obtained verification results with those reported in the
   article and in the `results/` directory.

## Verification Workflow

The overall workflow is:

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

## Notes

The repository contains the implementation, case-study data, Event-B model,
and supporting materials needed to inspect and reproduce the transformation
and formal verification workflow reported in the article.
