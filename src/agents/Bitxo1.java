package agents;

import java.lang.Math;

// Exemple de Bitxo
public class Bitxo1 extends Agent {

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

    public Bitxo1(Agents pare) {
        super(pare, "Bitxo1", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {

        // atributsAgents(v,w,dv,av,ll,es,hy)
<<<<<<< Updated upstream
        graus = 45;
        int cost = atributsAgent(7, 9, 600, graus, 40, 5, 0);
        System.out.println("Cost total:" + cost);
=======
        int cost = atributsAgent(5, 6, 600, 45, 30, 0, 0);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        mirant = false;
        random = new Random();
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
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
=======
            endavant();
        }
    }

    private void deteccioRecursos() {
        if (estat.veigAlgunRecurs) { //He quitado esto: estat.numObjectes > 0 && , ya que es redundante
            int distanciaMin = 9999; //He cambiado MAX_DIST_BALES por 9999 porque no tenia sentido
            int distanciaActualAliado;
            int distanciaActualEnemigo;

            int distanciaMinRecAliado = 99999;
            int distanciaMinRecEnemigo = MAX_DIST_BALES; //Aquí si que se puede usar

            Objecte objRecAliadoMasCercano = null;
            Objecte objRecEnemigoMasCercano = null;

            for (Objecte objActual : estat.objectes) { //Per a cada objecte
                if ((objActual != null) && (objActual.agafaSector() == 2 || objActual.agafaSector() == 3)) {
                    if (esRecursAliat(objActual)) {
                        distanciaActualAliado = objActual.agafaDistancia();
                        if (distanciaActualAliado < distanciaMinRecAliado) {
                            distanciaMinRecAliado = distanciaActualAliado;
                            objRecAliadoMasCercano = objActual;
                        }
                    } else if (((objActual.agafaTipus() >= 100 && !esRecursAliat(objActual)) 
                            || objActual.agafaTipus() == Estat.AGENT) && !estat.llançant) {
                        distanciaActualEnemigo = objActual.agafaDistancia();
                        if (distanciaActualEnemigo < distanciaMinRecEnemigo) {
                            distanciaMinRecEnemigo = distanciaActualEnemigo;
                            objRecEnemigoMasCercano = objActual;
                        }
                    } 
                }
            }

            //Salgo de este for con el recurso/agente enemigo y aliado más cercano 
            if (distanciaMinRecEnemigo < MAX_DIST_BALES
                    && estat.llançaments > 0 && !estat.llançant
                    && objRecEnemigoMasCercano != null) {
                mira(objRecEnemigoMasCercano);
                llança();
                mirant = true;
            }
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
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
=======
    private boolean esRecursAliat(Objecte obj) {
        return obj.agafaTipus() == (100 + estat.id)||obj.agafaTipus() == Estat.ESCUT;
    }

    private boolean repetirAccio() {
        if (repetir != 0) {
            switch (accio) {
                case ESQUERRA:
                    darrer_gir = 10 + random.nextInt(10);
                    break;
                case DRETA:
                    darrer_gir = -(10 + random.nextInt(10));
                    break;
                case VOLTEJ:
                    darrer_gir = 180 + (random.nextInt(90) - 45);
                    break;
                case DESFER:
                    darrer_gir = -(darrer_gir);
                    break;
            }
            gira(darrer_gir);
            comprobaMirant();
            repetir--;
            return true;
        } else {
            return false;
>>>>>>> Stashed changes
        }
    }

    private void cerca() {
        obj = estat.objectes;
        heTrobatPropi = false;
        heTrobatEnemic = false;
        recursPropiLocal = null;
        recursEnemicLocal = null;
        dist_recurs_Propia_Global = 9999;
        dist_recurs_Enemic_Global = 9999;
        for (Objecte o : obj) {
            if (o != null && o.agafaTipus() == (100 + estat.id)) {
                if (o.agafaDistancia() < dist_recurs_Propia_Global) {
                    dist_recurs_Propia_Global = o.agafaDistancia();
                    recursPropiGlobal = o;
                    heTrobatPropi = true;
                }
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
