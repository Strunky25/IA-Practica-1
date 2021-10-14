package agents;

// Exemple de Bitxo

import java.util.Random;

public class Bitxo1 extends Agent {

    static final int PARET = 0;
    static final int BITXO = 1;
    static final int RES = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;

    private Estat estat;
    private Random random;           
    private Accio accio;
    private int repetir, darrer_gir;
    private boolean mirant;

    public Bitxo1(Agents pare) {
        super(pare, "Lift", "imatges/robotank1.gif");
    }

    @Override
    public void inicia() {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 8, 600, 30, 23, 5, 5);
        System.out.println("Cost total: " + cost);
        // Inicialització de variables que utilitzaré al meu comportament
        repetir = 0;
        accio = Accio.ENDAVANT;
        mirant = false;
        random = new Random();
    }

    @Override
    public void avaluaComportament() {
        if(!repetirAccio()){ 
            estat = estatCombat();
            deteccioAliment();
            deteccioParet();         
        }
    }
    
    private void deteccioParet(){
        if(estat.enCollisio){
            accio = Accio.VOLTEJ;
            repetir = 1;
        }else if(estat.distanciaVisors[CENTRAL] < 30 &&
                estat.objecteVisor[CENTRAL] == PARET){
            if(random.nextBoolean()) accio = Accio.DRETA;
            else accio = Accio.ESQUERRA;
            repetir = 3;    
        } else if (estat.distanciaVisors[ESQUERRA] < 40 &&
                estat.objecteVisor[ESQUERRA] == PARET){
            accio = Accio.DRETA;
            repetir = 3;
        } else if (estat.distanciaVisors[DRETA] < 40 &&
                estat.objecteVisor[DRETA] == PARET){
            accio = Accio.ESQUERRA;
            repetir = 3;
        } else {
            endavant();
        }
    }
    
    private void deteccioAliment(){
        if(estat.numObjectes > 0 && estat.veigAlgunRecurs){
            int distanciaMin = Integer.MAX_VALUE;
            Objecte obj = null;
            for(Objecte objActual: estat.objectes){
                if(objActual != null){
                    int distanciaObjActual = objActual.agafaDistancia();
                    if(objActual.agafaTipus() == (100 + estat.id) &&
                            distanciaObjActual < distanciaMin){
                        distanciaMin = distanciaObjActual;
                        obj = objActual;
                    }
                }
            }
            if(obj != null){
                mira(obj);
                mirant = true;
            } else mirant = false;
        } else mirant = false;
    }
    
    private boolean repetirAccio(){
        if(repetir != 0){
            switch(accio){
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
        } else return false;
    }
    
    private void comprobaMirant(){
        if(mirant){
            accio = Accio.DESFER;
            repetir = 1;
        }
    }
    
    private enum Accio{
        ESQUERRA,
        DRETA,
        VOLTEJ,
        ENDAVANT, 
        DESFER
    }
}
