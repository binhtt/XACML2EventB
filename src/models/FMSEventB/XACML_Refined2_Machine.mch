	MACHINE
		XACML_Refined2_Machine 	›
	REFINES
	⚬	 XACML_Refined1_Machine 
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
	⚬	inv1:	decision = Permit ⇒
	 	     	    (∃pol,r·
	 	     	        pol ∈ applicable_policies ∧
	 	     	        r ∈ applicable_rules ∧
	 	     	        r ∈ policies(pol) ∧
	 	     	        effect(r) = Permit) not theorem ›
	⚬	inv2:	decision = Deny ⇒
	 	     	    (∃pol,r·
	 	     	        pol ∈ applicable_policies ∧
	 	     	        r ∈ applicable_rules ∧
	 	     	        r ∈ policies(pol) ∧
	 	     	        effect(r) = Deny)
	 	     	 not theorem ›
	⚬	inv3:	applicable_rules = ∅ ⇒
	 	     	    decision = NotApplicable not theorem ›
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

	⚬	EvaluateRequest:	 extended ordinary ›
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

	⚬	Combine_PermitOverrides_Permit :	 extended ordinary ›
		REFINES
		⚬	 Combine_PermitOverrides_Permit 
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = PermitOverrides not theorem ›
		⚬	grd2:	∃r·r ∈ applicable_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit not theorem ›
		THEN
		⚬	act1:	decision ≔ Permit ›
		END

	⚬	Combine_PermitOverrides_Deny:	 extended ordinary ›
		REFINES
		⚬	 Combine_PermitOverrides_Deny 
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

	⚬	Combine_PermitOverrides_NotApplicable:	 extended ordinary ›
		REFINES
		⚬	  Combine_PermitOverrides_NotApplicable 
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = PermitOverrides not theorem ›
		⚬	grd2:	applicable_rules ∩ policies(pol) = ∅ not theorem ›
		THEN
		⚬	act1:	decision ≔ NotApplicable ›
		END

	⚬	Combine_DenyOverrides_Deny:	 extended ordinary ›
		REFINES
		⚬	 Combine_DenyOverrides_Deny 
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

	⚬	Combine_DenyOverrides_Permit:	 extended ordinary ›
		REFINES
		⚬	 Combine_DenyOverrides_Permit 
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

	⚬	Combine_DenyOverrides_NotApplicable:	 extended ordinary ›
		REFINES
		⚬	 Combine_DenyOverrides_NotApplicable 
		ANY
		⚬	pol	 ›
		WHERE
		⚬	grd1:	pol ∈ applicable_policies ∧
		 	     	        combining_alg_policy(pol) = DenyOverrides not theorem ›
		⚬	grd2:	 applicable_rules ∩ policies(pol) = ∅ not theorem ›
		THEN
		⚬	act1:	decision ≔ NotApplicable ›
		END

	⚬	ApplyCombining_FirstApplicable:	 extended ordinary ›
		REFINES
		⚬	 ApplyCombining_FirstApplicable 
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

	⚬	Combine_FirstApplicable_NotApplicable:	 extended ordinary ›
		REFINES
		⚬	 Combine_FirstApplicable_NotApplicable 
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
