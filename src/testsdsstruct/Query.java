/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testsdsstruct;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import statement.Statement;

/**
 *
 * @author nicola.fusco
 */
public class Query {

    private String sql;
    private boolean negativo = false;           //se l'elemento selezionato è negativo
    private Scanner t;
    private String line;
    private Scanner l;
    String parola;                              //contiene la riga corrente
    String nquery = "";                         //Contiente la query nel formato richiesto
    int tab;                                    //tabulazioni
    int c_tab;                                  //conta i tab presenti ad ogni riga
    int const_tab;                              //conserva il valore di tab con cui si entra nella fun espressione
    private boolean a_capo = false;
    int aux;                                    //per il controllo delle parentesi
    String op;                                  //stringa per il recupero delle tabulazioni
    String be;
    //operazini per prelevare il numero di tab
    LinkedList operazioni = new LinkedList();
    LinkedList off_set = new LinkedList();
    LinkedList oper_logici = new LinkedList();
    Stack operandi = new Stack();
    LinkedList condizioni = new LinkedList();
    Pair<String, Integer> cond;
    Pair<String, Integer> op_lo;
    
    Statement statement;

    public Query(String s) {
        sql = s;
    }

    public void AnalizzaQuery() {

        String tipo;

        LinkedList operazioni;
        Stack operandi;
        tab = 0;

        t = new Scanner(sql);

        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();
        //SCRIVIAMO LA PRIMA RIGA CHE PUO ESSERE SELECT O SELECT ALL,DISTINCT E SALTIAMO LA RIGA COLUMNS

        while (t.hasNextLine()) {
            switch (parola) {
                case "GROUP":
                    analizzaGroup();
                    break;
                case "ORDER":
                    nquery += "\n";
                    analizzaOrder();
                    break;
                case "UNION":
                    analizzaUnion();
                    break;
                case "SELECT":      
                    tab += 1;
                    for (int i = 0; i < tab; i++) {
                        nquery += "\t";
                    }
                    nquery += parola + " ";
                    while (l.hasNext()) {
                        nquery += l.next() + " ";  // Gestiamo il caso in cui non ci sia sola una parola es select all, select distinct
                    }
                    nquery += "\n";
                    line = t.nextLine();            //Saltiamo la riga columns
                    analizzaSelect();
                    break;
                case "WHERE":
                    analizzaWhere();
                    break;
                case "FROM":
                    analizzaFrom();
                    break;
                case "HAVING":
                    analizzaHaving();
                    tab = tab - 1;
                    break;
            }
        }
 
        System.out.println("New query\n" + nquery);
    }

    /*
     * Questa funziona non analizza solo la clausola select ma tutte quelle che
     * ricevono nome di colonne e possibili funzioni come having,order by
     */
    public void analizzaSelect() {
        tab += 1;
        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();
        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        int con_tab = c_tab - 1;

        while (c_tab > con_tab && t.hasNextLine()) {

            if (parola.equals("AS")) {
                nquery += " " + line + "\n";
                while (l.hasNext()) {
                    l.next();  // Gestiamo il caso in cui l'alias non sia sola una parola
                }
            } else {
                if (!parola.equals("HAVING")) {
                    parola = toglitipo(parola);
                    if (!isOperation(parola)) {
                        if (!isFunzioneAggregazione(parola)) {
                            for (int i = 0; i < tab; i++) {
                                nquery += "\t";
                            }
                            parola = toglinometabella(parola);
                            if (negativo) {                       //Caso in cui si ha SELECT -A
                                nquery += "-";
                                negativo = false;
                            }
                            nquery += parola;
                            nquery += "\n";
                        } else {
                            for (int i = 0; i < tab; i++) {
                                nquery += "\t";
                            }
                            parola = FunzioneAggregazione(parola);
                            if (!parola.equals("AS")) {
                                nquery += parola + "\n";
                            }
                        }
                    } else {
                        espressione();
                    }
                } else {
                    break;
                }
            }
            if (t.hasNextLine()) {
                line = t.nextLine();
                l = new Scanner(line);
                parola = l.next();
                //operazini per prelevare il numero di tab
                op = line.substring(0, line.indexOf(parola));
                c_tab = (op.length());
            }
        }
        tab = tab - 1;
    }

    public void analizzaFrom() {
        String aux = "";                 //serve nel caso in cui c'è la clausola join e quindi le tabelle 
        //le prenderemo in seguiro
        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        nquery += parola + "\n";
        tab += 1;

        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();
        while (!(parola.equals("WHERE") || (parola.equals("JOINS"))) && t.hasNextLine()) {
            for (int i = 0; i < tab; i++) {
                aux += "\t";
            }
            aux += parola;
            aux += "\n";
            line = t.nextLine();
            l = new Scanner(line);
            parola = l.next();
        }
        tab = tab - 2;
        if (parola.equals("WHERE")) {
            nquery += aux;
            analizzaWhere();
        }
        if (parola.equals("JOINS")) {
            analizzaJoins();
        }
    }

    public void analizzaJoins() {

        tab = tab + 2;
        //leggiamo la prossima riga
        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();

        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        const_tab = c_tab - 1;

        while (c_tab != const_tab && t.hasNextLine()) {

            for (int i = 0; i < tab; i++) {
                nquery += "\t";
            }
            be = parola + " ";

            line = t.nextLine();
            l = new Scanner(line);
            parola = l.next();
            //abbiamo letto il nome della prima tabella

            parola += " " + be;

            line = t.nextLine();
            l = new Scanner(line);
            be = l.next();
            //abbiamo letto il nome della seconda tabella

            parola += be + " ON ( ";

            line = t.nextLine();
            //saltiamo la riga di ON
            line = t.nextLine();
            //saltiamo la riga dell uguale
            line = t.nextLine();
            l = new Scanner(line);
            be = l.next();

            be = toglitipo(be);
            be = toglinometabella(be);
            parola += be + " = ";
            //Abbiamo letto il primo campo su cui si effettua il join e lo abbiamo inserito nella string

            line = t.nextLine();
            l = new Scanner(line);
            be = l.next();

            be = toglitipo(be);
            be = toglinometabella(be);
            parola += be + " ) ";
            //Abbiamo letto il secondo campo su cui si effettua il join e lo abbiamo inserito nella string

            nquery += parola + "\n";

            line = t.nextLine();
            l = new Scanner(line);
            parola = l.next();
            op = line.substring(0, line.indexOf(parola));
            c_tab = (op.length());
        }
        tab = tab - 2;
    }

    public void analizzaWhere() {

        tab += 1;
        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        nquery += parola;
        tab += 1;

        condizioni();
        if (oper_logici.isEmpty()) {                          //caso in cui c'è una sola condizione
            cond = (Pair<String, Integer>) condizioni.pop();
            nquery += "\n";
            for (int i = 0; i < tab; i++) {
                nquery += "\t";
            }
            nquery += cond.getFirst();
        } else {
            op_lo = (Pair<String, Integer>) oper_logici.getFirst();
            stampawhile(op_lo);
        }

        oper_logici.clear();
        condizioni.clear();
        tab = tab - 1;
        nquery += "\n";        
    }

    public void analizzaGroup() {
        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        nquery += "GROUP BY" + "\n";
        tab += 1;
        
        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        const_tab = c_tab - 1;

        analizzaSelect();
        if (!parola.equals("HAVING")) {     
            tab = tab - 2;
        }
    }

    public void analizzaHaving() {
        tab = tab - 1;
        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        tab = tab + 1;
        nquery += parola;
        condizioni();
        if (oper_logici.isEmpty()) {                          //caso in cui c'è una sola condizione
            cond = (Pair<String, Integer>) condizioni.pop();
            nquery += "\n";
            for (int i = 0; i < tab; i++) {
                nquery += "\t";
            }
            nquery += cond.getFirst();
        } else {
            op_lo = (Pair<String, Integer>) oper_logici.getFirst();
            stampawhile(op_lo);
        }
        oper_logici.clear();
        condizioni.clear();
    }

    public void analizzaOrder() {

        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        nquery += parola + " ";
        while (l.hasNext()) {
            nquery += l.next() + " ";  // Gestiamo il caso in cui non ci sia sola una parola
        }
        nquery += "\n";

        l = new Scanner(line);
        parola = l.next();

        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        const_tab = c_tab - 1;

        analizzaSelect();
    }

    public void analizzaUnion() {
        nquery += "\n";
        for (int i = 0; i < tab; i++) {
            nquery += "\t";
        }
        nquery += parola + " ";
        while (l.hasNext()) {
            nquery += l.next() + " ";  // Gestiamo il caso in cui non ci sia sola una parola
        }
        nquery += "\n";
        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();
    }

    public void stampawhile(Pair<String, Integer> c) {
        int cont;
        if (!condizioni.isEmpty()) {
            cond = (Pair<String, Integer>) condizioni.getFirst();
        }
        if (!oper_logici.isEmpty()) {
            op_lo = (Pair<String, Integer>) oper_logici.removeFirst();
        }
        while (c.getSecond() < cond.getSecond() && !(condizioni.isEmpty())) {
            cond = (Pair<String, Integer>) condizioni.pop();
            if (((cond.getSecond()) - op_lo.getSecond()) <= 1) {
                cont = cond.getSecond();
                while (cont == cond.getSecond()) {
                    nquery += "\n";
                    for (int i = 0; i < tab; i++) {
                        nquery += "\t";
                    }
                    if (a_capo) {
                        nquery += op_lo.getFirst() + " ( " + "\t";
                        tab += 1;
                        nquery += cond.getFirst();
                        a_capo = false;
                    } else {
                        nquery += op_lo.getFirst() + " " + cond.getFirst();
                    }
                    if (!condizioni.isEmpty()) {
                        cond = (Pair<String, Integer>) condizioni.getFirst();
                        if (cont == cond.getSecond()) {
                            cond = (Pair<String, Integer>) condizioni.pop();
                        }
                    } else {
                        cond.setSecond(-1);
                    }
                }
            } else {
                if (!oper_logici.isEmpty()) {
                    condizioni.push(cond);
                    op_lo = (Pair<String, Integer>) oper_logici.getFirst();

                    a_capo = true;
                    // nquery += "\n";
                    stampawhile(op_lo);

                    if (!condizioni.isEmpty()) {
                        cond = (Pair<String, Integer>) condizioni.pop();
                        if (((cond.getSecond()) - c.getSecond()) <= 1) {
                            cont = cond.getSecond();

                            while (cont == cond.getSecond()) {
                                nquery += "\n";
                                for (int i = 0; i < tab; i++) {
                                    nquery += "\t";
                                }
                                nquery += c.getFirst() + " " + cond.getFirst();
                                if (!condizioni.isEmpty()) {
                                    cond = (Pair<String, Integer>) condizioni.getFirst();

                                    if (cont == cond.getSecond()) {
                                        cond = (Pair<String, Integer>) condizioni.pop();
                                    }
                                } else {
                                    cond.setSecond(-1);         //per settare la condizione di uscita
                                }
                            }
                            if (!condizioni.isEmpty()) {
                                nquery += "  ) ";
                                tab = tab - 1;
                            }
                        }
                    } else {
                        nquery += "  ) ";
                        tab = tab - 1;
                    }
                }
            }
        }
    }

    public String toglitipo(String parola) {
        // tipo=parola.substring(parola.indexOf("{"),parola.indexOf("}")+1 );
        String t = parola.substring(parola.indexOf("}") + 1);
        return t;
    }

    public String toglinometabella(String parola) {
        String t = parola.substring(0, 1);
        if (isOperation(t)) {
            negativo = true;
        }
        if (parola.indexOf(".") != -1) {
            if (isDoubleOrInt(parola) == -1) {
                parola = parola.substring(parola.indexOf(".") + 2);
                parola = parola.substring(0, parola.lastIndexOf("") - 1);
            }
        }
        return parola;
    }

    public void espressione() {

        int aux;                                    //per il controllo delle parentesi
        String op;                                  //stringa per il recupero delle tabulazioni
        LinkedList parentesi = new LinkedList();
        LinkedList operazioni = new LinkedList();
        Stack operandi = new Stack();

        if (parola.equals("*") || parola.equals("/")) {
            parentesi.add(1);
        }
        operazioni.add(parola);

        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();

        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        const_tab = c_tab - 1;

        while (c_tab != const_tab) {
            parola = toglitipo(parola);
            if (isOperation(parola)) {
                operazioni.add(parola);
                if (parola.equals("*") || parola.equals("/")) {
                    parentesi.add(c_tab);
                }
            } else {
                parola = toglinometabella(parola);
                if (negativo) {
                    operazioni.removeLast();
                    operazioni.add("-");
                    negativo = false;
                }
                if (!parentesi.isEmpty()) {
                    aux = (int) parentesi.pop();
                    if (operandi.isEmpty()) {
                        parola += " )";
                    } else {
                        op = (String) operandi.pop();
                        op += " )";
                        operandi.push(op);
                    }
                }
                operandi.push(parola);
            }
            line = t.nextLine();
            l = new Scanner(line);
            parola = l.next();
            op = line.substring(0, line.indexOf(parola));
            c_tab = (op.length());
        }
        //ricostruzione espressione
        while (!operandi.isEmpty()) {
            nquery += operandi.pop();
            if (!operazioni.isEmpty()) {
                op = (String) operazioni.pop();
                nquery += " " + op + " ";
                if (op.equals("*") || op.equals("/")) {
                    nquery += "( ";
                }
            }
        }
    }

    public void condizioni() {
        int c_in, cost_in;          //delimitatori per leggere la serie di condizioni della in
        String aux;
        line = t.nextLine();
        l = new Scanner(line);
        parola = l.next();

        //operazini per prelevare il numero di tab
        op = line.substring(0, line.indexOf(parola));
        c_tab = (op.length());

        //impostiamo la costante di tab
        const_tab = c_tab - 1;

        while (c_tab > const_tab && t.hasNextLine()) {
            if (isOperatoreLogico(parola)) {
                op_lo = new Pair<String, Integer>(parola, c_tab);
                oper_logici.add(op_lo);
                line = t.nextLine();
                l = new Scanner(line);
                parola = l.next();
                op = line.substring(0, line.indexOf(parola));
                c_tab = (op.length());
            } else {
                if (isOperatoreConfronto(parola)) {
                    be = parola + " ";

                    line = t.nextLine();
                    l = new Scanner(line);
                    parola = l.next();
                    parola = toglitipo(parola);

                    if (isFunzioneAggregazione(parola)) {
                        aux = FunzioneAggregazione(parola);
                        aux += " " + be;
                        be = parola;
                        parola = aux;
                    } else {
                        parola = toglinometabella(parola);
                        parola += " " + be;
                        line = t.nextLine();
                        l = new Scanner(line);
                        be = l.next();
                    }

                    be = toglitipo(be);
                    be = toglinometabella(be);

                    parola += " " + be;
                    cond = new Pair<String, Integer>(parola, c_tab);
                    condizioni.add(cond);

                    line = t.nextLine();
                    l = new Scanner(line);
                    parola = l.next();
                    op = line.substring(0, line.indexOf(parola));
                    c_tab = (op.length());

                } else {
                    if (!isFunzioneAggregazione(parola)) {
                        if (parola.equals("BETWEEN")) {
                            be = parola + " ";

                            line = t.nextLine();
                            l = new Scanner(line);
                            parola = l.next();
                            parola = toglitipo(parola);
                            parola = toglinometabella(parola);

                            parola += " " + be;

                            line = t.nextLine();
                            l = new Scanner(line);
                            be = l.next();
                            be = toglitipo(be);

                            parola += be + " AND ";

                            line = t.nextLine();
                            l = new Scanner(line);
                            be = l.next();
                            be = toglitipo(be);

                            parola += " " + be;

                            cond = new Pair<String, Integer>(parola, c_tab);
                            condizioni.add(cond);

                            line = t.nextLine();
                            l = new Scanner(line);
                            parola = l.next();
                            op = line.substring(0, line.indexOf(parola));
                            c_tab = (op.length());

                        } else {
                            if (parola.equals("IN")) {
                                be = parola + " ";

                                line = t.nextLine();
                                l = new Scanner(line);
                                parola = l.next();
                                op = line.substring(0, line.indexOf(parola));
                                cost_in = (op.length());
                                parola = toglitipo(parola);
                                parola = toglinometabella(parola);
                                c_in = cost_in;

                                parola += " " + be + " ( ";

                                line = t.nextLine();
                                l = new Scanner(line);
                                be = l.next();
                                op = line.substring(0, line.indexOf(be));
                                c_in = (op.length());

                                while (cost_in <= c_in) {
                                    be = toglitipo(be);
                                    parola += be + ", ";
                                    line = t.nextLine();
                                    l = new Scanner(line);
                                    be = l.next();
                                    op = line.substring(0, line.indexOf(be));
                                    c_in = (op.length());
                                }
                                c_in = parola.length();
                                parola = parola.substring(0, c_in - 2);
                                parola += " )";
                                cond = new Pair<String, Integer>(parola, c_tab);
                                condizioni.add(cond);
                                parola = be;
                            } //fine caso in
                        }// fine caso between
                    }
                }
            }
        }
    }

    public boolean isOperation(String parola) {
        switch (parola) {
            case "*":
                return true;
            case "+":
                return true;
            case "-":
                return true;
            case "/":
                return true;
            case "MOD":
                return true;
            case "DIV":
                return true;
            default:
                return false;
        }
    }

    public boolean isOperatoreLogico(String parola) {
        switch (parola) {
            case "AND":
                return true;
            case "OR":
                return true;
            case "XOR":
                return true;
            case "NOT":
                return true;
            default:
                return false;
        }
    }

    public boolean isOperatoreConfronto(String parola) {
        switch (parola) {
            case ">":
                return true;
            case ">=":
                return true;
            case "<":
                return true;
            case "<=":
                return true;
            case "<>":
                return true;
            case "=":
                return true;
            case "LIKE":
                return true;
            default:
                return false;
        }
    }

    public String FunzioneAggregazione(String p) {
        String espr = "";
        switch (p) {
            case "COUNT":
                espr += parola;                 //scrivo Count nella query
                line = t.nextLine();
                l = new Scanner(line);
                parola = l.next();
                espr += "(" + parola + ")";         //scorro la riga per prendere l'opratore *

                //L'ALIAS NON C' E SEMPRE POICHE POSSIAMO ESSERE IN UN HAVING               
                line = t.nextLine();
                l = new Scanner(line);
                parola = l.next();
                if (parola.equals("AS")) {
                    espr += " " + parola;
                    while (l.hasNext()) {
                        espr += " " + l.next();
                    }
                }
                return espr;
            default:
                nquery += parola;          //Scrivo funzione

                operazioni = new LinkedList();
                operandi = new Stack();

                line = t.nextLine();
                l = new Scanner(line);
                parola = l.next();

                parola = toglitipo(parola);
                nquery += " (";
                if (isOperation(parola)) {
                    espressione();
                } else {
                    nquery += " " + parola;
                    line = t.nextLine();
                    l = new Scanner(line);
                    parola = l.next();
                }
                nquery += " )";
                if (parola.equals("AS")) {
                    nquery += " " + parola;
                    while (l.hasNext()) {
                        nquery += " " + l.next();
                    }
                    nquery += "\n";
                }
                return parola;
        }
    }

    public boolean isFunzioneAggregazione(String parola) {
        switch (parola) {
            case "AVG":
                return true;
            case "COUNT":
                return true;
            case "FIRST":
                return true;
            case "LAST":
                return true;
            case "MAX":
                return true;
            case "MIN":
                return true;
            case "SUM":
                return true;
            default:
                return false;
        }
    }

    public int isDoubleOrInt(String num) {               //verifichiamo se è un numero
        try {
            Integer.parseInt(num);
            return 0;
        } catch (Exception e) {
            try {
                Double.parseDouble(num);
                return 1;
            } catch (Exception ed) {
                return -1;
            }
        }
    }
}
