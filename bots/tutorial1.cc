#include <sc2api/sc2_api.h>
#include <iostream>

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
    }


};

int main(int argc, char* argv[])
{
    Coordinator coordinator;
    coordinator.LoadSettings(argc, argv);
    //sc2::RenderSettings settings(10,10,0,0);

    //coordinator.SetRender(settings);
    coordinator.SetStepSize(10);
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