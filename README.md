# Evolving utility system through ANJI to play MicroRTS

The project takes root in the MicroRTS (https://github.com/santiontanon/microrts) in combination with the NEAT implementation ANJI (http://nn.cs.utexas.edu/?anji)

The purpose of this project was to experiment with evolving utility system based AI through a NeuroEvolution of Augmenting Topologies (NEAT) method. 

The Utility System implementation is located under 'src/ai/utilitySystem' and the AI-agent using Utility Systems is located in the 'src/ai'.

All the ANJI related code is located in the ANJI directory and all MicroRTS is in the src directory.

Under src is the AnjiIntegration directory where most of the code to integrate microRTS and ANJI is located, together with the main methods for starting evolutions and evaluations.

## Instructions for running the compiled jar files in finalBuild:
For some ease of use, the most prominent tests have been prepared to be run from the finalBuild in the form of a java jar file.

Included is 3 bat scripts:

- startFrontend.bat: boots up the GUI window to play around with base microRTS
- startQuicktest.bat: runs a mini evolution from baseline against baseline to see if everything works.
- startAllTests.bat: runs all the tests described in the paper.

if you want to run an evolution with your own config, it can be done with the following command (from the finalBuild directory):

    'java -cp MicroRTS_EUS.jar properties\utility_system_properties\quickTest.properties'

Where the quickTest.properties is then replaced with the properties file you want to run.
You can take inspiration in the properties files under finalBuild\properties\utility_system_properties and modify or copy/paste them as you see fit.
Our custom properties are located in the top of the properties file, the others are from the ANJI project. 
to learn more about them look in the docs under ANJI/docs/index.html

After a run is done the results can be found in a directory called storage where the ANJI chromosome that lead to the best utility system can be located, 
together with a txt file that can visually show the utility system if its content is copied to this site -> http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000. 
Furthermore, it also creates a file that gives an overview over the fitness scores doing the evolution generations 'fitness.XML'. To view a graph open the 'visualization/index.html'
and select the fitness.xml file you want to view. 
It also contains the results from the evaluation 'evalResultsP1.csv' & 'evalResultsP2.csv' 
where the utility system has respectively been evaluated as player 1 and player 2 based on the evolution method chosen in the properties file.

## Compiling a new jar file
If you want to modify the code and generate a new runnable jar file, there is an ANT build located in the base layer called 'buildMicroRTS_EUS.xml' which can compile a new jar and copies all the necessary files to 'out/CombinedBuild' directory.
We found the easiest to use the intellij IDE which has build-in ANT build support.
