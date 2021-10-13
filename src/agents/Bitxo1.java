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
    private int repetir;

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
            accio = Accio.ENRERE;
            repetir = 1;
        }else if(estat.distanciaVisors[CENTRAL] < 30 && estat.objecteVisor[CENTRAL] == PARET){
            if(random.nextBoolean()) accio = Accio.DRETA;
            else accio = Accio.ESQUERRA;
            repetir = 3;    
        } else if (estat.distanciaVisors[ESQUERRA] < 40 && estat.objecteVisor[ESQUERRA] == PARET){
            accio = Accio.DRETA;
            repetir = 3;
        } else if (estat.distanciaVisors[DRETA] < 40 && estat.objecteVisor[DRETA] == PARET){
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
                int distanciaObjActual;
                if(objActual != null && objActual.agafaTipus() == (100 + estat.id) && (distanciaObjActual = objActual.agafaDistancia()) < distanciaMin){
                    distanciaMin = distanciaObjActual;
                    obj = objActual;
                }
            }
            mira(obj);
        }
    }
    
    private boolean repetirAccio(){
        if(repetir != 0){
            switch(accio){
                case ESQUERRA:
                    gira(10 + random.nextInt(10)); 
                    break;
                case DRETA:
                    gira(-(10 + random.nextInt(10)));;
                    break;
                case ENRERE:
                    enrere();
                    if(repetir == 1) gira(180 + (random.nextInt(90) - 45));
                    break;
                case ENDAVANT:
                    endavant(); 
                    break;
            }
            repetir--;
            return true;
        } else return false;
    }
    
    public enum Accio{
        ESQUERRA,
        DRETA,
        ENRERE,
        ENDAVANT,
        MIRANT,
    }
}
