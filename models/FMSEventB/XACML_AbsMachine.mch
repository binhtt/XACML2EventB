	MACHINE
		XACML_AbsMachine 	›
	SEES
	⚬	 XACML_Context 
	VARIABLES
	⚬	current_request	 ›
	⚬	decision	 ›
	⚬	applicable_rules	 ›
	⚬	env	 ›
	INVARIANTS
	⚬	inv1:	current_request ∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT not theorem ›
	⚬	inv2:	decision ∈ DECISION not theorem ›
	⚬	inv3:	applicable_rules ⊆ RULE not theorem ›
	⚬	inv4:	env∈ENVIRONMENT not theorem ›
	EVENTS
	⚬	INITIALISATION:	 not extended ordinary ›
		THEN
		⚬	act1:	current_request :∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT ›
		⚬	act2:	decision ≔ NotApplicable ›
		⚬	act3:	applicable_rules ≔ ∅ ›
		⚬	act4:	env:∈ENVIRONMENT ›
		END

	⚬	EvaluateRequest :	 not extended ordinary ›
		ANY
		⚬	req_subj	 ›
		⚬	req_res	 ›
		⚬	req_act	 ›
		⚬	req_env	 ›
		WHERE
		⚬	grd1:	current_request = (req_subj↦req_res↦req_act↦req_env) not theorem ›
		THEN
		⚬	act1:	applicable_rules ≔ {r ∣ r ∈ RULE ∧(req_subj↦ req_res↦ req_act↦ req_env)∈ 
		 	     	target(r) ∧ condition(r)(req_subj↦ req_res↦ req_act↦ req_env) = TRUE} ›
		⚬	act2:	decision ≔ NotApplicable ›
		END

	⚬	ApplyRule :	 not extended ordinary ›
		ANY
		⚬	r	 ›
		WHERE
		⚬	grd1:	r ∈ applicable_rules not theorem ›
		THEN
		⚬	act1:	decision ≔ effect(r) ›
		END

	END
