	MACHINE
		XACML_Refined1_Machine 	›
	REFINES
	⚬	 XACML_AbsMachine 
	SEES
	⚬	 XACML_Refined1_Context 
	VARIABLES
	⚬	current_request	 ›
	⚬	decision	 ›
	⚬	applicable_rules	 ›
	⚬	applicable_policies	 ›
	⚬	applicable_policy_sets	 ›
	⚬	env	 ›
	INVARIANTS
	⚬	inv1:	applicable_policies ⊆ POLICY not theorem ›
	⚬	inv2:	applicable_policy_sets ⊆ POLICY_SET not theorem ›
	EVENTS
	⚬	INITIALISATION:	 extended ordinary ›
		THEN
		⚬	act1:	current_request :∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT ›
		⚬	act2:	decision ≔ NotApplicable ›
		⚬	act3:	applicable_rules ≔ ∅ ›
		⚬	act4:	env:∈ENVIRONMENT ›
		⚬	act5:	applicable_policies≔∅ ›
		⚬	act6:	applicable_policy_sets≔∅ ›
		END

	⚬	EvaluateRequest :	 extended ordinary ›
		REFINES
		⚬	 EvaluateRequest  
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
		⚬	act4:	applicable_policies ≔ {p ∣ p ∈ POLICY ∧ 
		 	     	                              (req_subj↦ req_res↦ req_act↦ req_env) ∈ policy_target(p)} ›
		⚬	act3:	applicable_policy_sets ≔ {ps ∣ ps ∈ POLICY_SET ∧ 
		 	     	                                  (req_subj↦ req_res↦ req_act↦ req_env) ∈ policy_set_target(ps)} ›
		END

	⚬	Combine_PermitOverrides_Permit:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = PermitOverrides not theorem ›
		⚬	grd2:	∃r·r ∈ applicable_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit not theorem ›
		THEN
		⚬	act1:	decision ≔ Permit ›
		END

	⚬	Combine_PermitOverrides_Deny:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = PermitOverrides not theorem ›
		⚬	grd2:	¬(∃r·r ∈ applicable_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit) not theorem ›
		⚬	grd3:	∃r·r ∈ applicable_rules ∧ r ∈ policies(pol) ∧ effect(r) = Deny not theorem ›
		THEN
		⚬	act1:	decision ≔ Deny ›
		END

	⚬	 Combine_PermitOverrides_NotApplicable:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = PermitOverrides not theorem ›
		⚬	grd2:	applicable_rules ∩ policies(pol) = ∅ not theorem ›
		THEN
		⚬	act1:	decision ≔ NotApplicable ›
		END

	⚬	ApplyCombining_FirstApplicable:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		⚬	r	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = FirstApplicable not theorem ›
		⚬	grd2:	r ∈ applicable_rules ∧ r ∈ policies(pol) not theorem ›
		⚬	grd3:	 ∀s·s ∈ policies(pol) ∧ s ∈ applicable_rules ⇒ rule_order(pol)(r) ≤ rule_order(pol)(s) not theorem ›
		THEN
		⚬	act1:	decision ≔ effect(r) ›
		END

	⚬	Combine_DenyOverrides_Deny:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	       combining_alg_policy(pol) = DenyOverrides not theorem ›
		⚬	grd2:	∃r·
		 	     	            r ∈ applicable_rules ∧
		 	     	            r ∈ policies(pol) ∧
		 	     	            effect(r) = Deny not theorem ›
		THEN
		⚬	act1:	decision ≔ Deny ›
		END

	⚬	Combine_DenyOverrides_Permit:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = DenyOverrides not theorem ›
		⚬	grd2:	¬(∃r·
		 	     	            r ∈ applicable_rules ∧
		 	     	            r ∈ policies(pol) ∧
		 	     	            effect(r) = Deny) not theorem ›
		⚬	grd3:	∃r·
		 	     	            r ∈ applicable_rules ∧
		 	     	            r ∈ policies(pol) ∧
		 	     	            effect(r) = Permit not theorem ›
		THEN
		⚬	act1:	decision ≔ Permit ›
		END

	⚬	Combine_DenyOverrides_NotApplicable:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = DenyOverrides not theorem ›
		⚬	grd2:	 applicable_rules ∩ policies(pol) = ∅ not theorem ›
		THEN
		⚬	act1:	decision ≔ NotApplicable ›
		END

	⚬	Combine_FirstApplicable_NotApplicable:	 not extended ordinary ›
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	 pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = FirstApplicable
		 	     	 not theorem ›
		⚬	grd2:	applicable_rules ∩ policies(pol) = ∅ not theorem ›
		THEN
		⚬	act1:	decision ≔ NotApplicable ›
		END

	END
