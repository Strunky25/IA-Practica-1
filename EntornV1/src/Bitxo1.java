package agents;

// Exemple de Bitxo

public class Bitxo1 extends Agent
{
    static final int PARET = 0;
    static final int BITXO   = 1;
    static final int RES   = -1;

    static final int ESQUERRA = 0;
    static final int CENTRAL  = 1;
    static final int DRETA    = 2;

    Estat estat;

    public Bitxo1(Agents pare) {
        super(pare, "Exemple1", "imatges/robotank1.gif");
    }

    @Override
    public void inicia()
    {
        // atributsAgents(v,w,dv,av,ll,es,hy)
        int cost = atributsAgent(6, 5, 600, 30, 23, 5, 5);
        System.out.println("Cost total:"+cost);
        
        // Inicialització de variables que utilitzaré al meu comportament
        
    }

    @Override
    public void avaluaComportament()
    {
        endavant();
    }
    
    
}