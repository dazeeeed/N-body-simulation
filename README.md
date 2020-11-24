# N - body movement simulation

This app is designed to simulate movement of N bodies in two dimensional space (X,Y). Number of bodies (particles) and its parameters such as initial position, initial velocity and mass is being chosen by the user. There is also an option for saving and loading collected data to/from a text file or a database.

## Body movement

Movement is based on the gravitational interaction and calculated using Newton's Formula. The calculation uses position as a current pixel a particle is being drawn on. 
> Pixels are integers so mathematical calculations must be rounded to the nearest integers. This sometimes causes a miscalculation of the velocity and thus acceleration.

## Database management

One of the options is saving/loading data from H2 database using an SQL language. Using that data we can recreate simulation, or analyze it (in another program). 

## Origin

This project was created as an graded assessment in object oriented programming class with my friend. Link to the original repo below.
> https://gitlab.com/Krzysztof1a/java-simulation
