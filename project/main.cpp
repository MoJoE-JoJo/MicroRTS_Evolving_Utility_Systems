#include <sc2api/sc2_api.h>
#include "Common/bot_examples.h"

#include <iostream>

using namespace sc2;

class Bot : public Agent {
public:
    virtual void OnGameStart() final {
        std::cout << "Hello, World!" << std::endl;
    }

    virtual void OnStep() final {
        //std::cout << Observation()->GetGameLoop() << std::endl;
		std::cout << Observation()->GetUnits().size() << std::endl;

    }
};

int main(int argc, char* argv[]) {
    Coordinator coordinator;
    coordinator.LoadSettings(argc, argv);

    Bot bot;
	coordinator.SetRealtime(false);
	coordinator.SetStepSize(10);
    coordinator.SetParticipants({
        CreateParticipant(Race::Terran, &bot),
        CreateComputer(Race::Zerg)
    });

    coordinator.LaunchStarcraft();
    coordinator.StartGame(sc2::kMapBelShirVestigeLE);

    while (coordinator.Update()) {
    }

	coordinator.StartGame(sc2::kMapBelShirVestigeLE);

	while (coordinator.Update())
	{
	}

    return 0;
}