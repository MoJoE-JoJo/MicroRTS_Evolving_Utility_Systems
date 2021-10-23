
#include "bot_test.h"

#include <iostream>
#include <string>
#include <algorithm>
#include <random>
#include <iterator>

#include "sc2api/sc2_api.h"
#include "sc2lib/sc2_lib.h"

using namespace sc2;
    
void TestBot::OnGameStart() {
    std::cout << "Hello, World!" << std::endl;
    std::cout << Observation()->GetUnits(Unit::Alliance::Self).size() << std::endl;

}

void TestBot::OnStep() {
    //std::cout << Observation()->GetGameLoop() << std::endl;
    //std::cout << Observation()->GetArmyCount() << std::endl;
    //std::cout << Observation()->GetIdleWorkerCount() << std::endl;
    //std::cout << Observation()->GetUnits(Unit::Neutral).size() << std::endl;
    std::cout << Observation()->GetUnits().size() << std::endl;
    std::cout << Observation()->GetUnits(Unit::Alliance::Self).size() << std::endl;
    std::cout << Observation()->GetUnits(Unit::Alliance::Enemy).size() << std::endl;

}

void TestBot::OnGameEnd()
{
    //std::cout << Observation()->GetResults()[0].player_id;
    ///std::cout << Observation()->GetResults()[0].result;
    //std::cout << "\n";
    //std::cout << Observation()->GetResults()[1].player_id;
    //std::cout << Observation()->GetResults()[1].result;
    //std::cout << "\n";
}
