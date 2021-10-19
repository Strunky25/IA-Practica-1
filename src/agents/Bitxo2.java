package agents;

/*
    BITXO WALLIS
*/
public class Bitxo2 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int ENDAVANT = 1;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int ENRERE = 3;
    static final int DONAVOLTA = 4;

    static final int ALERTA = 0;
    static final int A = 1;
    static final int B = 2;

    int repetir;
    int accio;
    int estatBitxo;
    int objI;
    boolean EsticMirant;
    //boolean heMirat;
    Estat estat;
    Objecte[] obj;

    //Esto nos permite encontrar el recurso nuestro mas cercano
    Objecte recursPropiLocal;
    Objecte recursPropiGlobal;
    Objecte recursEnemicLocal;
    Objecte recursEnemicGlobal;
    int dist_recurs_Propia_Global;
    int dist_recurs_Propia_Local;
    int dist_recurs_Enemic_Global;
    int dist_recurs_Enemic_Local;
    boolean heTrobatPropi;
    boolean heTrobatEnemic;

    int sectorLocal;
    int sectorGlobal;

    int recursosActuals;

    int graus;
    int girs;

    int polla = 0;

    public Bitxo2(Agents pare) {
        super(pare, "Wallis", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {

        // atributsAgents(v,w,dv,av,ll,es,hy)
        graus = 45;
        int cost = atributsAgent(7, 9, 600, graus, 40, 5, 0);
        System.out.println("Cost total:" + cost);

        // Inicialització de variables que utilitzaré al meu comportament   
        repetir = 0;
        objI = 0;
        estatBitxo = ALERTA;
        EsticMirant = false;
        heTrobatPropi = false;
        // heMirat = false;
        sectorLocal = 0;
        sectorGlobal = 0;
        recursosActuals = 0;

        dist_recurs_Propia_Global = 9999;

        girs = 1;//(int) Math.ceil(360 / graus);
    }

    @Override
    public void avaluaComportament() {
        estat = estatCombat();
        if (repetir != 0) {
            repetirAccio();
            repetir--;
        } else {
            if (estat.enCollisio) {
                atura();
                accio = ENRERE;
                repetir = 5;
            } else {
                atura();
                cerca();
                //HAY QUE HACER QUE SI LO QUE ESTÁ VIENDO ES UN ENEMIGO EN VEZ DE UN RECURSO 
                //ENEMIGO DISPARE ÚNICAMENTE SI ESTÁ A MENOS DE 150PX (P. EJE)
                if (heTrobatEnemic && (estat.llançaments > 0)
                        && !estat.llançant && dist_recurs_Enemic_Global < 405) { 
                    mira(recursEnemicGlobal);
                    llança();
                }
                if (heTrobatPropi) {
                    mira(recursPropiGlobal);
                    endavant();
                }

                if (estat.distanciaVisors[ESQUERRA] < 20 && (estat.objecteVisor[ESQUERRA] == PARET)) {
                    accio = DRETA;
                    repetir = 1;
                } else if (estat.distanciaVisors[DRETA] < 20 && (estat.objecteVisor[DRETA] == PARET)) {
                    accio = ESQUERRA;
                    repetir = 1;
                }
                endavant();
            }
        }
    }

    private void repetirAccio() {
        switch (accio) {
            case 0:
                //esquerra();
                gira(30);
                break;
            case 1:
                endavant();
                break;
            case 2:
                //dreta();
                gira(-30);
                break;
            case 3:
                enrere();
                if (repetir == 1) {
                    gira(30);
                }
                break;
            case 4:
                gira(graus * 2);
                cerca();
                sectorLocal++;
                break;
        }
    }

    private void cerca() {
        obj = estat.objectes;
        heTrobatPropi = false;
        heTrobatEnemic = false;
        dist_recurs_Propia_Global = 9999;
        dist_recurs_Enemic_Global = 9999;
        
        
        
        for (Objecte o : obj) { //Recorrido Objetos
            //Pillar recurso aliado mas cercano
            if (o != null && o.agafaTipus() == (100 + estat.id)) {
                if (o.agafaDistancia() < dist_recurs_Propia_Global) {
                    dist_recurs_Propia_Global = o.agafaDistancia();
                    recursPropiGlobal = o;
                    heTrobatPropi = true;
                }
                //Pillar recurso enemigo mas cercano
            } else if (o != null && ((o.agafaTipus() >= 100) && (o.agafaTipus() != (100 + estat.id)) || o.agafaTipus() == Estat.AGENT)) {
                if (o.agafaDistancia() < dist_recurs_Enemic_Global) {
                    dist_recurs_Enemic_Global = o.agafaDistancia();
                    recursEnemicGlobal = o;
                    heTrobatEnemic = true;
                }
            }
        }
    }
}
