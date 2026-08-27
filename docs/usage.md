# Usage

XACML2Event-B provides a graphical interface for transforming supported XACML
policy structures into Event-B models.

## Starting the Tool

Run:

    xacml2evtb.Xacml2EventBTool

The graphical interface provides three main functions:

- `Xacml2EvtB`: transformation of XACML policies into Event-B components.
- `Txt2Xacml`: conversion of structured textual rule descriptions into XACML.
- `Xacml2Txt`: extraction of rule information from XACML into a textual form.

## XACML to Event-B Transformation

1. Select the `Xacml2EvtB` tab.
2. Click **Browse .xml** and select an XACML policy file, or paste the XACML
   policy directly into the input area.
3. Click **Convert -> Event-B**.
4. The generated Event-B components are displayed in the preview area.
5. Click **Save all** to save the generated components.

The transformation generates the following Event-B components:

    XACML_Context.ctx
    XACML_Refined1_Context.ctx
    XACML_AbsMachine.mch
    XACML_Refined1_Machine.mch
    XACML_Refined2_Machine.mch

These components correspond to the successive modeling and refinement levels
described in the article.

## Supported Policy Structures

The current implementation supports the XACML constructs considered by the
framework, including:

- PolicySet
- Policy
- Rule
- Permit and Deny rule effects
- PermitOverrides
- DenyOverrides
- FirstApplicable

The Event-B decision domain additionally includes `NotApplicable` as an
evaluation outcome.

## Case-Study Examples

The policy data used for the case studies are organized under:

    examples/
    ├── HACS/
    ├── FMS/
    └── SIS/

These directories correspond to the Healthcare Access Control System (HACS),
Financial Management System (FMS), and Student Information System (SIS)
evaluated in the article.

## Event-B Models

The Event-B verification model is provided under:

    models/FMSEventB/

The model contains the contexts and machines corresponding to the
transformation and refinement structure described in the article.
