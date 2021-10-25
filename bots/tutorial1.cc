#include <sc2api/sc2_api.h>

#include <iostream>

#include "sc2utils/sc2_arg_parser.h"
#include "sc2utils/sc2_manage_process.h"

#include "bot_test.h"
#include "bot_examples.h"



//*************************************************************************************************
int main(int argc, char* argv[])
{
    sc2::Coordinator coordinator;
    if (!coordinator.LoadSettings(argc, argv))
    {
        return 1;
    }

    coordinator.SetRealtime(true);
    //coordinator.SetStepSize(100);

    // Add the custom bot, it will control the player.
    TestBot bot;
    coordinator.SetParticipants({
        CreateParticipant(sc2::Race::Terran, &bot),
        CreateComputer(sc2::Race::Zerg),
        });

    // Start the game.
    coordinator.LaunchStarcraft();
    const char* newMap = "Ladder/TestModified.SC2Map";
    coordinator.StartGame(newMap);

    while (coordinator.Update())
    {
    }

    return 0;
}