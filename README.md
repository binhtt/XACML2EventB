# XACML2Event-B

This repository accompanies the paper **"XACML2Event-B: A formal approach for verifying XACML access control policies using Event-B"**.  
It provides datasets, a support tool, and Event-B models for reproducing the experiments and verification results.

---

## 📂 Repository Structure

XACML2Event-B/
├─ data/ # Experimental datasets (HACS, FMS, SIS)
├─ tool/ # Java tool for transformations
└─ rodin_models/ # Event-B models (.ctx, .mch)

---

## 📊 Datasets

The repository contains three experimental systems: **HACS**, **FMS**, and **SIS**.

- **Txt2Xacml_rules.json**: mapping from textual rules to XACML rules.  
- **XACML_policies.json**: grouping of rules into policies.  
- **FMS_45rules_18policies.json**: the complete FMS dataset in a single file.  

### Statistics
- **HACS**: ~42 rules, grouped into multiple policies.  
- **FMS**: 45 rules, 18 policies.  
- **SIS**: ~38 rules, grouped into multiple policies.  

---

## 🔧 Tool

The Java tool provides three main modules:

- **Txt2Xacml**: convert rules from natural text into XACML rules.  
- **Xacml2Txt**: convert XACML rules back to text.  
- **Xacml2EvtB**: transform XACML policies into Event-B models (.ctx, .mch).  

🧩 Event-B Models

The rodin_models/ folder contains Event-B contexts and machines generated from XACML policies.

Import into Rodin

Open Rodin IDE.

Create a new project or import: File → Import → Rodin Project.

Select the rodin_models/ directory.

Open .ctx and .mch files and run the prover.

Contents

XACML_Context.ctx: defines SUBJECT, RESOURCE, ACTION, ENVIRONMENT, RULE, DECISION.

XACML_AbsMachine.mch: abstract machine with EvaluateRequest, ApplyRule events.

XACML_Ref1_Ctx.ctx / XACML_Ref1_Mach.mch: first refinement layer.
XACML_Ref2_Mach.mch: seccond refinement layer.
