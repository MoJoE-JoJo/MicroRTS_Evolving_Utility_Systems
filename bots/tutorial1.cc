#include <sc2api/sc2_api.h>

#include <iostream>

#include "sc2utils/sc2_arg_parser.h"
#include "sc2utils/sc2_manage_process.h"

#include "bot_test.h"
#include "bot_examples.h"


/*
using namespace sc2;

class Bot : public Agent
{
public:
    virtual void OnGameStart() final
    {
        std::cout << "Hello, World!" << std::endl;
    }

    virtual void OnStep() final
    {
        std::cout << Observation()->GetGameLoop() << std::endl;
        std::cout << Observation()->GetUnits(sc2::Unit::Alliance::Self).size() << std::endl;
    }

    virtual void OnGameEnd() final
    {
        std::cout << Observation()->GetResults()[0].player_id;
        std::cout << Observation()->GetResults()[0].result;
        std::cout << "\n";
        std::cout << Observation()->GetResults()[1].player_id;
        std::cout << Observation()->GetResults()[1].result;
        std::cout << "\n";
    }

};

int main(int argc, char* argv[])
{
    Coordinator coordinator;
    coordinator.LoadSettings(argc, argv);
    //sc2::RenderSettings settings(10,10,0,0);
    coordinator.SetRealtime(true);
    //coordinator.SetRender(settings);
    coordinator.SetStepSize(100);
    Bot bot;
    coordinator.SetParticipants({
    CreateParticipant(Race::Terran, &bot),
    CreateComputer(Race::Zerg)
        });
    coordinator.LaunchStarcraft();
    int gamesCounter = 0;

    while (gamesCounter < 2)
    {
        gamesCounter++;
        coordinator.StartGame(sc2::kMapBelShirVestigeLE);
        while (coordinator.Update())
        {
        }
    }
    return 0;
}
*/



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
    const char* newMap = "Example/TestMap1.SC2Map";
    coordinator.StartGame(newMap);

    while (coordinator.Update())
    {
    }

    return 0;
}