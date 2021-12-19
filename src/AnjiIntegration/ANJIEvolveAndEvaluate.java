package AnjiIntegration;

import ai.utilitySystem.UtilitySystem;
import com.anji.neat.Evolver;
import com.anji.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ANJIEvolveAndEvaluate {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            String array[] = {"1", "2", "3", "4", "5", "9", "11", "12", "13", "14"};
            for (String s : array) {
                run(new Properties("utility_system_properties/test" + s + ".properties"));
            }
        }
        else
        {
            System.out.println(System.getProperty("user.dir"));
            System.out.println(args[0]);
            Properties props = new Properties();
            props.load(new FileInputStream(args[0]));
            run(props);
        }
        System.exit(0);
    }

    private static void run(Properties props) {
        try {

            // === RUN EVOLUTION ===
            Evolver evolver = new Evolver();
            evolver.init(props);
            evolver.run();

            //sleep 3 sec to ensure the .db files gets saved to disk
            Thread.sleep(3000);

            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            // after evolving is done, save backups of the results in another folder

            // create file structure: storage/{prop.name}
            String runName = props.getProperty("run.name");
            String filepath = "./storage/" + runName;
            new File(filepath).mkdirs();

            // save chromosome as XML
            String chromSrc = "./db/chromosome/chromosome" + evolver.getChamp().getId() + ".xml";

            Path src = Paths.get(chromSrc);
            Path dst = Paths.get(filepath + "/" + runName + "_ChampionChromosome.xml");

            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);

            // copy and rename fitness XML file
            String fitnessFileSrc = "./nevt/fitness/fitness.xml";

            Path fitnessSrc = Paths.get(fitnessFileSrc);
            Path fitnessDst = Paths.get(filepath + "/" + runName + "_fitness.xml");

            Files.copy(fitnessSrc, fitnessDst, StandardCopyOption.REPLACE_EXISTING);

            // create UML of resulting utility system and save in a txt.
            anjiConverter conv = new anjiConverter();
            UtilitySystem utilSystem = conv.toUtilitySystemFromChromosome(evolver.getChamp().getId());

            String uml = utilSystem.toPlantUML();

            Path umlPath = Paths.get(filepath + "/" + runName + "_uml.txt");

            try {
                Files.writeString(umlPath, uml, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                // Handle exception
            }

            // evaluate the champion utility system.
            EvaluateChromosome.eval(props, utilSystem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

