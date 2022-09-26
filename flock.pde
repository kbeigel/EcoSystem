// The Flock (a list of Prey objects)

class Flock {
    ArrayList<Prey> preys; // An ArrayList for all the preys

    Flock() {
        // Initialize the ArrayList
        preys = new ArrayList<Prey>();
        // FLOCK SETUP
        for (int i = 0; i < initialPreys; i++) 
        {
            addPrey(new Prey(width/2, height/2));
        }
    }
    //Update method happens every time it is called in the draw method in Ecosystem
    void Update(ArrayList<Plankton> planktons) {
        ArrayList<Prey> dying = new ArrayList<Prey>();
        if (preys.size() > 0) {
            for (Prey prey : preys) {
                // Passing the entire list of preys to each prey individually
                prey.Update(preys, planktons); 
                //boolean, "if prey is dying"
                if (prey.dying())
                {
                    // prey is added to the dying array list
                    dying.add(prey);
                }
            }

            for (Prey prey : dying)
            {
                preys.remove(prey);
            }
        }
    }

    void addPrey(Prey b) {
        preys.add(b);
    }

    int getSize() {
        return preys.size();
    }
}