package xacml2evtb;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Revised XACML2Event-B prototype aligned with the revised manuscript.
 * Supported hierarchy: PolicySet -> Policy -> Rule.
 * Supported outcomes: Permit, Deny, NotApplicable.
 * Supported combining algorithms: PermitOverrides, DenyOverrides, FirstApplicable.
 */
public class Xacml2EventBTool extends JFrame {
    private JTextArea xacmlInput, eventBPreview, txtInputArea, xacmlToTxtInputArea;
    private JButton browseBtn, convertBtn, saveBtn, browseTxtBtn, convertTxtToXacmlBtn,
            saveTxtToXmlBtn, browseXacmlBtn, convertXacmlToTxtBtn, saveXacmlToTxtBtn;
    private JFileChooser chooser;
    private String lastConvertedXml = "", lastConvertedTxt = "";
    private EventBModel lastModel;

    public Xacml2EventBTool() {
        setTitle("XACML2Event-B Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        JPanel evtbPanel = new JPanel(new BorderLayout(10,10));
        evtbPanel.setBorder(new EmptyBorder(10,10,10,10));
        xacmlInput = monoArea(18,80);
        JScrollPane in = new JScrollPane(xacmlInput);
        in.setBorder(BorderFactory.createTitledBorder("XACML input"));
        JPanel c1 = new JPanel();
        browseBtn = new JButton("Browse .xml");
        convertBtn = new JButton("Convert -> Event-B");
        saveBtn = new JButton("Save all"); saveBtn.setEnabled(false);
        c1.add(browseBtn); c1.add(convertBtn); c1.add(saveBtn);
        JPanel top = new JPanel(new BorderLayout(6,6)); top.add(in,BorderLayout.CENTER); top.add(c1,BorderLayout.SOUTH);
        eventBPreview = monoArea(22,80); eventBPreview.setEditable(false);
        JScrollPane out = new JScrollPane(eventBPreview);
        out.setBorder(BorderFactory.createTitledBorder("Generated Event-B preview"));
        evtbPanel.add(top,BorderLayout.NORTH); evtbPanel.add(out,BorderLayout.CENTER);
        tabs.add("Xacml2EvtB", evtbPanel);

        JPanel t2x = new JPanel(new BorderLayout(8,8)); t2x.setBorder(new EmptyBorder(10,10,10,10));
        txtInputArea = monoArea(18,80);
        JScrollPane ts = new JScrollPane(txtInputArea);
        ts.setBorder(BorderFactory.createTitledBorder("RuleId | Effect | Target | Condition"));
        JPanel c2 = new JPanel();
        browseTxtBtn = new JButton("Browse .txt"); convertTxtToXacmlBtn = new JButton("Convert -> XACML");
        saveTxtToXmlBtn = new JButton("Save XML"); saveTxtToXmlBtn.setEnabled(false);
        c2.add(browseTxtBtn); c2.add(convertTxtToXacmlBtn); c2.add(saveTxtToXmlBtn);
        t2x.add(ts,BorderLayout.CENTER); t2x.add(c2,BorderLayout.SOUTH); tabs.add("Txt2Xacml", t2x);

        JPanel x2t = new JPanel(new BorderLayout(8,8)); x2t.setBorder(new EmptyBorder(10,10,10,10));
        xacmlToTxtInputArea = monoArea(18,80);
        JScrollPane xs = new JScrollPane(xacmlToTxtInputArea);
        xs.setBorder(BorderFactory.createTitledBorder("XACML input"));
        JPanel c3 = new JPanel();
        browseXacmlBtn = new JButton("Browse .xml"); convertXacmlToTxtBtn = new JButton("Convert -> TXT");
        saveXacmlToTxtBtn = new JButton("Save TXT"); saveXacmlToTxtBtn.setEnabled(false);
        c3.add(browseXacmlBtn); c3.add(convertXacmlToTxtBtn); c3.add(saveXacmlToTxtBtn);
        x2t.add(xs,BorderLayout.CENTER); x2t.add(c3,BorderLayout.SOUTH); tabs.add("Xacml2Txt", x2t);

        add(tabs);
        chooser = new JFileChooser();
        browseBtn.addActionListener(e -> browseXml(xacmlInput));
        browseXacmlBtn.addActionListener(e -> browseXml(xacmlToTxtInputArea));
        browseTxtBtn.addActionListener(e -> browseTxt());
        convertBtn.addActionListener(e -> convertEventB());
        saveBtn.addActionListener(e -> saveEventB());
        convertTxtToXacmlBtn.addActionListener(e -> txtToXacml());
        saveTxtToXmlBtn.addActionListener(e -> saveSingle(lastConvertedXml,"xml"));
        convertXacmlToTxtBtn.addActionListener(e -> xacmlToTxt());
        saveXacmlToTxtBtn.addActionListener(e -> saveSingle(lastConvertedTxt,"txt"));
    }

    private static JTextArea monoArea(int r,int c){ JTextArea a=new JTextArea(r,c); a.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12)); return a; }

    private void browseXml(JTextArea area){
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("XACML/XML","xml","xacml"));
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try{ area.setText(Files.readString(chooser.getSelectedFile().toPath(),StandardCharsets.UTF_8)); }
            catch(IOException ex){ error(ex); }
        }
    }
    private void browseTxt(){
        chooser.setFileFilter(new FileNameExtensionFilter("Text","txt"));
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try{ txtInputArea.setText(Files.readString(chooser.getSelectedFile().toPath(),StandardCharsets.UTF_8)); }
            catch(IOException ex){ error(ex); }
        }
    }

    private void convertEventB(){
        try{
            XACMLModel m = XACMLParser.parseFromString(xacmlInput.getText());
            lastModel = EventBGenerator.generate(m);
            eventBPreview.setText("==== XACML_Context.ctx ====\n"+lastModel.ctx+"\n\n==== XACML_Refined1_Context.ctx ====\n"+lastModel.ctxRef1+
                    "\n\n==== XACML_AbsMachine.mch ====\n"+lastModel.absMch+"\n\n==== XACML_Refined1_Machine.mch ====\n"+lastModel.ref1Mch+
                    "\n\n==== XACML_Refined2_Machine.mch ====\n"+lastModel.ref2Mch);
            eventBPreview.setCaretPosition(0); saveBtn.setEnabled(true);
        } catch(Exception ex){ error(ex); }
    }

    private void saveEventB(){
        if(lastModel==null) return;
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION){ chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); return; }
        File d=chooser.getSelectedFile(); if(!d.exists()) d.mkdirs();
        try{
            write(d,"XACML_Context.ctx",lastModel.ctx); write(d,"XACML_Refined1_Context.ctx",lastModel.ctxRef1);
            write(d,"XACML_AbsMachine.mch",lastModel.absMch); write(d,"XACML_Refined1_Machine.mch",lastModel.ref1Mch);
            write(d,"XACML_Refined2_Machine.mch",lastModel.ref2Mch);
            JOptionPane.showMessageDialog(this,"Saved five files to "+d.getAbsolutePath());
        }catch(IOException ex){ error(ex); }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }
    private static void write(File d,String n,String s)throws IOException{ Files.writeString(d.toPath().resolve(n),s,StandardCharsets.UTF_8); }

    private void txtToXacml(){
        try{
            StringBuilder x=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Policy PolicyId=\"P1\" RuleCombiningAlgId=\"first-applicable\">\n");
            int k=1;
            for(String raw:txtInputArea.getText().split("\\R")){
                if(raw.trim().isEmpty()) continue;
                String[] p=raw.split("\\|",4); String id=p.length>0&&!p[0].trim().isEmpty()?p[0].trim():"R"+(k++);
                String eff=p.length>1?normalizeEffect(p[1]):null; if(eff==null) throw new IllegalArgumentException("Effect must be Permit or Deny for "+id);
                String tar=p.length>2?p[2].trim():"", con=p.length>3?p[3].trim():"";
                x.append("  <Rule RuleId=\"").append(xml(id)).append("\" Effect=\"").append(eff).append("\">\n")
                 .append("    <Target>").append(xml(tar)).append("</Target>\n");
                if(!con.isEmpty()) x.append("    <Condition>").append(xml(con)).append("</Condition>\n");
                x.append("  </Rule>\n");
            }
            x.append("</Policy>\n"); lastConvertedXml=x.toString(); preview("Generated XACML",lastConvertedXml); saveTxtToXmlBtn.setEnabled(true);
        }catch(Exception ex){ error(ex); }
    }
    private void xacmlToTxt(){
        try{
            XACMLModel m=XACMLParser.parseFromString(xacmlToTxtInputArea.getText()); StringBuilder b=new StringBuilder();
            for(RuleModel r:m.rules) b.append(r.id).append(" | ").append(r.effect).append(" | ").append(one(r.targetRaw)).append(" | ").append(one(r.conditionRaw)).append("\n");
            lastConvertedTxt=b.toString(); preview("Generated TXT",lastConvertedTxt); saveXacmlToTxtBtn.setEnabled(true);
        }catch(Exception ex){ error(ex); }
    }
    private void preview(String title,String s){ JTextArea a=monoArea(30,100); a.setText(s); a.setEditable(false); JOptionPane.showMessageDialog(this,new JScrollPane(a),title,JOptionPane.INFORMATION_MESSAGE); }
    private void saveSingle(String s,String ext){ if(s==null||s.isEmpty())return; chooser.setFileFilter(new FileNameExtensionFilter(ext.toUpperCase(),ext)); if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){ File f=chooser.getSelectedFile(); if(!f.getName().toLowerCase().endsWith("."+ext)) f=new File(f.getParentFile(),f.getName()+"."+ext); try{Files.writeString(f.toPath(),s,StandardCharsets.UTF_8);}catch(IOException ex){error(ex);} } }
    private void error(Exception ex){ JOptionPane.showMessageDialog(this,ex.getMessage(),"XACML2Event-B",JOptionPane.ERROR_MESSAGE); }

    static final class RuleModel { final String id,effect,targetRaw,conditionRaw; RuleModel(String i,String e,String t,String c){id=i;effect=e;targetRaw=t==null?"":t;conditionRaw=c==null?"":c;} }
    static final class PolicyModel { final String id,alg,targetRaw; final List<String> rules=new ArrayList<>(); PolicyModel(String i,String a,String t){id=i;alg=a;targetRaw=t==null?"":t;} }
    static final class PolicySetModel { final String id,alg,targetRaw; final List<String> policies=new ArrayList<>(); PolicySetModel(String i,String a,String t){id=i;alg=a;targetRaw=t==null?"":t;} }
    static final class XACMLModel { final List<RuleModel> rules=new ArrayList<>(); final List<PolicyModel> policies=new ArrayList<>(); final List<PolicySetModel> policySets=new ArrayList<>(); }

    static final class XACMLParser {
        static XACMLModel parseFromString(String xml)throws Exception{
            DocumentBuilderFactory f=DocumentBuilderFactory.newInstance(); f.setNamespaceAware(true);
            try{f.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);}catch(Exception ignored){}
            try{f.setFeature("http://xml.org/sax/features/external-general-entities",false);}catch(Exception ignored){}
            DocumentBuilder db=f.newDocumentBuilder(); Document doc=db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            XACMLModel m=new XACMLModel(); Set<String> seenRules=new HashSet<>();
            NodeList pns=doc.getElementsByTagNameNS("*","Policy"); if(pns.getLength()==0)pns=doc.getElementsByTagName("Policy");
            for(int i=0;i<pns.getLength();i++){
                Element p=(Element)pns.item(i); String pid=nz(p.getAttribute("PolicyId"),"Policy"+(i+1));
                PolicyModel pm=new PolicyModel(pid,alg(p.getAttribute("RuleCombiningAlgId")),childText(p,"Target"));
                for(Element r:children(p,"Rule")){
                    String rid=nz(r.getAttribute("RuleId"),"R"+(m.rules.size()+1)); if(!seenRules.add(rid))throw new IllegalArgumentException("Duplicate RuleId: "+rid);
                    String eff=normalizeEffect(r.getAttribute("Effect")); if(eff==null)throw new IllegalArgumentException("Unsupported Effect for "+rid+": "+r.getAttribute("Effect"));
                    m.rules.add(new RuleModel(rid,eff,childText(r,"Target"),childText(r,"Condition"))); pm.rules.add(rid);
                }
                m.policies.add(pm);
            }
            NodeList sns=doc.getElementsByTagNameNS("*","PolicySet"); if(sns.getLength()==0)sns=doc.getElementsByTagName("PolicySet");
            for(int i=0;i<sns.getLength();i++){
                Element ps=(Element)sns.item(i); Node par=ps.getParentNode(); if(par instanceof Element && "PolicySet".equals(local((Element)par))) throw new IllegalArgumentException("Nested PolicySet is outside the supported fragment");
                PolicySetModel sm=new PolicySetModel(nz(ps.getAttribute("PolicySetId"),"PolicySet"+(i+1)),alg(first(ps.getAttribute("PolicyCombiningAlgId"),ps.getAttribute("PolicySetCombiningAlgId"))),childText(ps,"Target"));
                for(Element p:children(ps,"Policy")){ String id=p.getAttribute("PolicyId"); if(id!=null&&!id.isBlank())sm.policies.add(id.trim()); }
                m.policySets.add(sm);
            }
            if(m.policies.isEmpty()||m.rules.isEmpty())throw new IllegalArgumentException("The supported fragment requires at least one Policy and one Rule");
            return m;
        }
    }

    static final class EventBModel { final String ctx,ctxRef1,absMch,ref1Mch,ref2Mch; EventBModel(String a,String b,String c,String d,String e){ctx=a;ctxRef1=b;absMch=c;ref1Mch=d;ref2Mch=e;} }

    static final class EventBGenerator {
        static EventBModel generate(XACMLModel m){ return new EventBModel(baseCtx(m),refCtx(m),absMachine(),ref1Machine(),ref2Machine()); }
        private static String baseCtx(XACMLModel m){
            StringBuilder b=new StringBuilder("CONTEXT XACML_Context\nSETS\n    SUBJECT\n    RESOURCE\n    ACTION\n    ENVIRONMENT\n    RULE\n    DECISION\nCONSTANTS\n    target\n    condition\n    effect\n    Permit\n    Deny\n    NotApplicable\n");
            for(RuleModel r:m.rules)b.append("    ").append(name(r.id)).append("\n");
            b.append("AXIOMS\n    axm1: target ∈ RULE → ℙ(SUBJECT × RESOURCE × ACTION × ENVIRONMENT)\n    axm2: condition ∈ RULE → (SUBJECT × RESOURCE × ACTION × ENVIRONMENT → BOOL)\n    axm3: effect ∈ RULE → DECISION\n    axm4: partition(DECISION, {Permit}, {Deny}, {NotApplicable})\n    axm5: NotApplicable ∉ ran(effect)\n");
            b.append("    axm_rules: partition(RULE"); for(RuleModel r:m.rules)b.append(", {").append(name(r.id)).append("}"); b.append(")\n");
            int i=1; for(RuleModel r:m.rules){ b.append("    axm_effect_").append(i++).append(": effect(").append(name(r.id)).append(") = ").append(r.effect).append("\n"); if(!r.targetRaw.isBlank())b.append("    // XACML Target[").append(name(r.id)).append("]: ").append(comment(r.targetRaw)).append("\n"); if(!r.conditionRaw.isBlank())b.append("    // XACML Condition[").append(name(r.id)).append("]: ").append(comment(r.conditionRaw)).append("\n"); }
            return b.append("END\n").toString();
        }
        private static String refCtx(XACMLModel m){
            StringBuilder b=new StringBuilder("CONTEXT XACML_Refined1_Context\nEXTENDS XACML_Context\nSETS\n    POLICY\n    POLICY_SET\n    COMBINING_ALGORITHM\nCONSTANTS\n    policies\n    policy_set_policies\n    policy_target\n    policy_set_target\n    PermitOverrides\n    DenyOverrides\n    FirstApplicable\n    combining_alg_policy\n    combining_alg_policyset\n    rule_order\n");
            for(PolicyModel p:m.policies)b.append("    ").append(name(p.id)).append("\n"); for(PolicySetModel ps:m.policySets)b.append("    ").append(name(ps.id)).append("\n");
            b.append("AXIOMS\n    axm1: policies ∈ POLICY → ℙ(RULE)\n    axm2: policy_set_policies ∈ POLICY_SET → ℙ(POLICY)\n    axm3: policy_target ∈ POLICY → ℙ(SUBJECT × RESOURCE × ACTION × ENVIRONMENT)\n    axm4: policy_set_target ∈ POLICY_SET → ℙ(SUBJECT × RESOURCE × ACTION × ENVIRONMENT)\n    axm5: partition(COMBINING_ALGORITHM, {PermitOverrides}, {DenyOverrides}, {FirstApplicable})\n    axm6: combining_alg_policy ∈ POLICY → COMBINING_ALGORITHM\n    axm7: combining_alg_policyset ∈ POLICY_SET → COMBINING_ALGORITHM\n    axm8: rule_order ∈ POLICY → (RULE → ℕ)\n    axm9: ∀pol·pol ∈ POLICY ⇒ ∀r1,r2·r1 ∈ policies(pol) ∧ r2 ∈ policies(pol) ∧ r1 ≠ r2 ⇒ rule_order(pol)(r1) ≠ rule_order(pol)(r2)\n");
            b.append("    axm_policies: partition(POLICY"); for(PolicyModel p:m.policies)b.append(", {").append(name(p.id)).append("}"); b.append(")\n");
            if(!m.policySets.isEmpty()){ b.append("    axm_policy_sets: partition(POLICY_SET"); for(PolicySetModel ps:m.policySets)b.append(", {").append(name(ps.id)).append("}"); b.append(")\n"); }
            int i=1; for(PolicyModel p:m.policies){ b.append("    axm_policy_rules_").append(i).append(": policies(").append(name(p.id)).append(") = {"); names(b,p.rules); b.append("}\n    axm_policy_alg_").append(i).append(": combining_alg_policy(").append(name(p.id)).append(") = ").append(p.alg).append("\n    axm_rule_order_").append(i).append(": rule_order(").append(name(p.id)).append(") = {"); for(int j=0;j<p.rules.size();j++){if(j>0)b.append(", ");b.append(name(p.rules.get(j))).append(" ↦ ").append(j+1);} b.append("}\n"); if(!p.targetRaw.isBlank())b.append("    // XACML Policy Target[").append(name(p.id)).append("]: ").append(comment(p.targetRaw)).append("\n"); i++; }
            i=1; for(PolicySetModel ps:m.policySets){ b.append("    axm_policy_set_membership_").append(i).append(": policy_set_policies(").append(name(ps.id)).append(") = {"); names(b,ps.policies); b.append("}\n    axm_policy_set_alg_").append(i).append(": combining_alg_policyset(").append(name(ps.id)).append(") = ").append(ps.alg).append("\n"); if(!ps.targetRaw.isBlank())b.append("    // XACML PolicySet Target[").append(name(ps.id)).append("]: ").append(comment(ps.targetRaw)).append("\n"); i++; }
            return b.append("END\n").toString();
        }
        private static String absMachine(){return """
MACHINE XACML_AbsMachine
SEES XACML_Context
VARIABLES
    curr_req
    decision
    appl_rules
    env
INVARIANTS
    inv1: curr_req ∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT
    inv2: decision ∈ DECISION
    inv3: appl_rules ⊆ RULE
    inv4: env ∈ ENVIRONMENT
EVENTS
    INITIALISATION
        THEN
            act1: curr_req :∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT
            act2: decision ≔ NotApplicable
            act3: appl_rules ≔ ∅
            act4: env :∈ ENVIRONMENT
        END
    EvaluateRequest
        ANY req_subj req_res req_act req_env
        WHERE
            grd1: curr_req = req_subj ↦ req_res ↦ req_act ↦ req_env
        THEN
            act1: appl_rules ≔ {r | r ∈ RULE ∧ (req_subj ↦ req_res ↦ req_act ↦ req_env) ∈ target(r) ∧ condition(r)(req_subj ↦ req_res ↦ req_act ↦ req_env) = TRUE}
            act2: decision ≔ NotApplicable
        END
    ApplyRule
        ANY r
        WHERE
            grd1: r ∈ appl_rules
        THEN
            act1: decision ≔ effect(r)
        END
END
""";}
        private static String ref1Machine(){ StringBuilder b=new StringBuilder("""
MACHINE XACML_Refined1_Machine
REFINES XACML_AbsMachine
SEES XACML_Refined1_Context
VARIABLES
    curr_req
    decision
    appl_rules
    appl_policies
    appl_policy_sets
    env
INVARIANTS
    inv1: appl_policies ⊆ POLICY
    inv2: appl_policy_sets ⊆ POLICY_SET
EVENTS
    INITIALISATION
        THEN
            act1: curr_req :∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT
            act2: decision ≔ NotApplicable
            act3: appl_rules ≔ ∅
            act4: env :∈ ENVIRONMENT
            act5: appl_policies ≔ ∅
            act6: appl_policy_sets ≔ ∅
        END
    EvaluateRequest
        REFINES EvaluateRequest
        ANY req_subj req_res req_act req_env
        WHERE
            grd1: curr_req = req_subj ↦ req_res ↦ req_act ↦ req_env
        THEN
            act1: appl_rules ≔ {r | r ∈ RULE ∧ (req_subj ↦ req_res ↦ req_act ↦ req_env) ∈ target(r) ∧ condition(r)(req_subj ↦ req_res ↦ req_act ↦ req_env) = TRUE}
            act2: appl_policies ≔ {p | p ∈ POLICY ∧ curr_req ∈ policy_target(p)}
            act3: appl_policy_sets ≔ {ps | ps ∈ POLICY_SET ∧ curr_req ∈ policy_set_target(ps)}
            act4: decision ≔ NotApplicable
        END
"""); combineEvents(b,false); return b.append("END\n").toString(); }
        private static String ref2Machine(){ StringBuilder b=new StringBuilder("""
MACHINE XACML_Refined2_Machine
REFINES XACML_Refined1_Machine
SEES XACML_Refined1_Context
VARIABLES
    curr_req
    decision
    appl_rules
    appl_policies
    appl_policy_sets
    env
INVARIANTS
    inv1: decision = Permit ⇒ ∃pol,r·pol ∈ appl_policies ∧ r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit
    inv2: decision = Deny ⇒ ∃pol,r·pol ∈ appl_policies ∧ r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Deny
    inv3: appl_rules = ∅ ⇒ decision = NotApplicable
EVENTS
    INITIALISATION
        THEN
            act1: curr_req :∈ SUBJECT × RESOURCE × ACTION × ENVIRONMENT
            act2: decision ≔ NotApplicable
            act3: appl_rules ≔ ∅
            act4: env :∈ ENVIRONMENT
            act5: appl_policies ≔ ∅
            act6: appl_policy_sets ≔ ∅
        END
    EvaluateRequest
        REFINES EvaluateRequest
        ANY req_subj req_res req_act req_env
        WHERE
            grd1: curr_req = req_subj ↦ req_res ↦ req_act ↦ req_env
        THEN
            act1: appl_rules ≔ {r | r ∈ RULE ∧ (req_subj ↦ req_res ↦ req_act ↦ req_env) ∈ target(r) ∧ condition(r)(req_subj ↦ req_res ↦ req_act ↦ req_env) = TRUE}
            act2: appl_policies ≔ {p | p ∈ POLICY ∧ curr_req ∈ policy_target(p)}
            act3: appl_policy_sets ≔ {ps | ps ∈ POLICY_SET ∧ curr_req ∈ policy_set_target(ps)}
            act4: decision ≔ NotApplicable
        END
"""); combineEvents(b,true); return b.append("END\n").toString(); }
        private static void combineEvents(StringBuilder b,boolean refine){
            event(b,"Combine_PermitOverrides_Permit",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = PermitOverrides\n            grd3: ∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit","Permit",false);
            event(b,"Combine_PermitOverrides_Deny",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = PermitOverrides\n            grd3: ¬(∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit)\n            grd4: ∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Deny","Deny",false);
            event(b,"Combine_PermitOverrides_NotApplicable",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = PermitOverrides\n            grd3: appl_rules ∩ policies(pol) = ∅","NotApplicable",false);
            event(b,"Combine_DenyOverrides_Deny",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = DenyOverrides\n            grd3: ∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Deny","Deny",false);
            event(b,"Combine_DenyOverrides_Permit",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = DenyOverrides\n            grd3: ¬(∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Deny)\n            grd4: ∃r·r ∈ appl_rules ∧ r ∈ policies(pol) ∧ effect(r) = Permit","Permit",false);
            event(b,"Combine_DenyOverrides_NotApplicable",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = DenyOverrides\n            grd3: appl_rules ∩ policies(pol) = ∅","NotApplicable",false);
            b.append("    ApplyCombining_FirstApplicable\n"); if(refine)b.append("        REFINES ApplyRule\n"); b.append("        ANY pol r\n        WHERE\n            grd1: pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = FirstApplicable\n            grd3: r ∈ appl_rules ∧ r ∈ policies(pol)\n            grd4: ∀s·s ∈ policies(pol) ∧ s ∈ appl_rules ⇒ rule_order(pol)(r) ≤ rule_order(pol)(s)\n        THEN\n            act1: decision ≔ effect(r)\n        END\n");
            event(b,"Combine_FirstApplicable_NotApplicable",refine,"pol ∈ appl_policies\n            grd2: combining_alg_policy(pol) = FirstApplicable\n            grd3: appl_rules ∩ policies(pol) = ∅","NotApplicable",false);
        }
        private static void event(StringBuilder b,String n,boolean ref,String guards,String decision,boolean unused){ b.append("    ").append(n).append("\n"); if(ref)b.append("        REFINES ApplyRule\n"); b.append("        ANY pol\n        WHERE\n            grd1: ").append(guards).append("\n        THEN\n            act1: decision ≔ ").append(decision).append("\n        END\n"); }
    }

    private static List<Element> children(Element p,String wanted){ List<Element> r=new ArrayList<>(); NodeList ns=p.getChildNodes(); for(int i=0;i<ns.getLength();i++)if(ns.item(i) instanceof Element e && wanted.equals(local(e)))r.add(e); return r; }
    private static String childText(Element p,String wanted){ for(Element e:children(p,wanted))return e.getTextContent()==null?"":e.getTextContent().trim(); return ""; }
    private static String local(Element e){ return e.getLocalName()!=null?e.getLocalName():(e.getTagName().contains(":")?e.getTagName().substring(e.getTagName().indexOf(':')+1):e.getTagName()); }
    private static String normalizeEffect(String s){ if(s==null)return null; if("Permit".equalsIgnoreCase(s.trim()))return "Permit"; if("Deny".equalsIgnoreCase(s.trim()))return "Deny"; return null; }
    private static String alg(String s){ if(s==null||s.isBlank())return "FirstApplicable"; String x=s.toLowerCase().replace("-","").replace("_",""); if(x.contains("permitoverrides"))return "PermitOverrides"; if(x.contains("denyoverrides"))return "DenyOverrides"; if(x.contains("firstapplicable"))return "FirstApplicable"; throw new IllegalArgumentException("Unsupported combining algorithm: "+s); }
    private static String first(String...v){ for(String s:v)if(s!=null&&!s.isBlank())return s; return ""; }
    private static String nz(String s,String f){ return s==null||s.isBlank()?f:s.trim(); }
    private static String name(String s){ String x=nz(s,"Item").replaceAll("[^A-Za-z0-9_]","_"); return x.matches("^[0-9].*")?"_"+x:x; }
    private static void names(StringBuilder b,List<String> ids){ for(int i=0;i<ids.size();i++){if(i>0)b.append(", ");b.append(name(ids.get(i)));} }
    private static String xml(String s){ return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;"); }
    private static String one(String s){ return s==null?"":s.replaceAll("\\s+"," ").trim(); }
    private static String comment(String s){ return one(s).replace("*/","* /"); }

    public static void main(String[] args){ SwingUtilities.invokeLater(()->{ Xacml2EventBTool t=new Xacml2EventBTool(); t.setVisible(true); }); }
}
