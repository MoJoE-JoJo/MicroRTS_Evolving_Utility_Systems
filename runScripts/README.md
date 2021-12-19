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
o learn more about them look in the docs under ANJI/docs/index.html

After a run is done the results can be found in a directory called storage where the ANJI chromosome that lead to the best utility system can be located, 
together with a txt file that can visually show the utility system if its concent is copied to this site -> http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000. 
Furthermore it also creates a file that gives an overview over the fitness scores doing the evolution generations (fitness.XML), to view a graph it open the 'visualization/index.html'
and select the fitness.xml file you want to view. 
It also contains the results from the evaluation 'evalResultsP1.csv' & 'evalResultsP2.csv' 
where the utility system has respectively been evaluated as player 1 and player 2.




