class Algae 
{ 
  ArrayList<PVector> circles;
  float minRad=0, maxRad= 1;
  float distanceToCenter;
  float newR; 
  float newX; 
  float newY;
  float margin;
  int growthTimer;
  

  Algae() {
    float initialX = random(0, width);
    float initialY = random(0, height);

    initialize();

    circles = new ArrayList<PVector>();
    circles.add(new PVector (initialX, initialY, random(minRad, maxRad)));
  }

  void initialize()
  {
    colorMode(RGB); 
    margin = 50;
    growthTimer = 0;
  }

  void Update()
  {
    fill(0, 130, 10); //base color
    
    if (growthTimer >= 4 && circles.size() > 0) 
    {
      noStroke();
      smooth();
      newR = random(minRad, maxRad);
      newX = random(-margin + newR, margin + width-newR);
      newY = random(-margin + newR, margin + height-newR);
      distanceToCenter = dist (newX, newY, circles.get(0).x, circles.get(0).y);
     

      if (distanceToCenter < 300) {

        float closestDist = 100000000;
        int closestIndex = 0;
        float distance;

        // which circle is the closest?
        for (int i=0; i < circles.size(); i++)
        {
          distance = dist(newX, newY, circles.get(i).x, circles.get(i).y);
          if (distance < closestDist) {
            closestDist = distance;
            closestIndex = i;
          }
        }
       

        // align it to the closest circle outline
        float angle = atan2(newY-circles.get(closestIndex).y, newX-circles.get(closestIndex).x);
        float deltaX = cos(angle) *circles.get(closestIndex).z;
        float deltaY = sin(angle) * circles.get(closestIndex).z;

        // draw it
        fill(55, 170, 30); //flash color
        newR =  exp(map (closestDist, 0, width, 1, 7));
        newX = circles.get(closestIndex).x  + deltaX;
        newY =  circles.get(closestIndex).y + deltaY;
        circles.add(new PVector (newX, newY, newR));
      }

        
        growthTimer = 0;         
    }
      for (int i = 0; i < circles.size(); i++)
      ellipse(circles.get(i).x, circles.get(i).y, circles.get(i).z*2, circles.get(i).z*2 );
      growthTimer++;
  }
}
