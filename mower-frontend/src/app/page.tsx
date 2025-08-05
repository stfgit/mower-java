'use client';

import React, { useState, useEffect } from 'react';
import { LawnGrid } from '@/components/LawnGrid';
import { MowerControls } from '@/components/MowerControls';
import { LawnSettings } from '@/components/LawnSettings';
import { MowerAPI } from '@/lib/api';
import { MowerPosition, LawnDimensions } from '@/types/mower';

export default function Home() {
  const [lawnDimensions, setLawnDimensions] = useState<LawnDimensions>({ width: 5, height: 5 });
  const [mowerPosition, setMowerPosition] = useState<MowerPosition>({ x: 1, y: 2, direction: 'N' });
  const [mowerPath, setMowerPath] = useState<Array<{ x: number; y: number }>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [status, setStatus] = useState<string>('En attente...');
  const [apiStatus, setApiStatus] = useState<'connected' | 'disconnected' | 'checking'>('checking');

  // Health check on component mount
  useEffect(() => {
    checkApiHealth();
  }, []);

  const checkApiHealth = async () => {
    try {
      setApiStatus('checking');
      await MowerAPI.healthCheck();
      setApiStatus('connected');
      setStatus('Connexion API établie');
    } catch (error) {
      console.error('API Health check failed:', error);
      setApiStatus('disconnected');
      setStatus('Erreur: Impossible de se connecter à l\'API');
    }
  };

  const handleCellClick = (x: number, y: number) => {
    if (x < lawnDimensions.width && y < lawnDimensions.height) {
      setMowerPosition({ ...mowerPosition, x, y });
      setStatus(`Tondeuse placée en position (${x}, ${y})`);
    }
  };

  const executeMowerCommand = async (commands: string) => {
    if (apiStatus !== 'connected') {
      setStatus('Erreur: API non disponible');
      return;
    }

    setIsLoading(true);
    setStatus(`Exécution des commandes: ${commands}...`);

    try {
      const request = {
        lawnDimensions: `${lawnDimensions.width - 1} ${lawnDimensions.height - 1}`,
        mowerPosition: `${mowerPosition.x} ${mowerPosition.y} ${mowerPosition.direction}`,
        commands: commands
      };

      const response = await MowerAPI.executeSingleMower(request);
      
      // Update mower position from response
      setMowerPosition({
        x: response.x,
        y: response.y,
        direction: response.direction
      });

      // Add current position to path
      setMowerPath(prev => [...prev, { x: response.x, y: response.y }]);
      
      setStatus(`✓ Commandes exécutées. Position finale: (${response.x}, ${response.y}) ${response.direction}`);
    } catch (error) {
      console.error('Erreur lors de l\'exécution:', error);
      setStatus(`Erreur: ${error instanceof Error ? error.message : 'Erreur inconnue'}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleExecuteCommand = async (command: string) => {
    await executeMowerCommand(command);
  };

  const handleExecuteSequence = async (sequence: string) => {
    await executeMowerCommand(sequence);
  };

  const resetMower = () => {
    setMowerPosition({ x: 1, y: 2, direction: 'N' });
    setMowerPath([]);
    setStatus('Tondeuse réinitialisée');
  };

  const clearPath = () => {
    setMowerPath([]);
    setStatus('Chemin effacé');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <header className="text-center mb-8">
          <h1 className="text-4xl font-bold text-green-700 mb-2">
            🌱 MowItNow - Contrôle de Tondeuse
          </h1>
          <div className="flex items-center justify-center gap-4">
            <div className={`px-3 py-1 rounded-full text-sm font-medium ${
              apiStatus === 'connected' ? 'bg-green-100 text-green-800' :
              apiStatus === 'disconnected' ? 'bg-red-100 text-red-800' :
              'bg-yellow-100 text-yellow-800'
            }`}>
              API: {apiStatus === 'connected' ? 'Connectée' : apiStatus === 'disconnected' ? 'Déconnectée' : 'Vérification...'}
            </div>
            <div className="text-gray-600">{status}</div>
          </div>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Left Column - Lawn Visualization */}
          <div className="space-y-6">
            <LawnSettings 
              dimensions={lawnDimensions}
              onDimensionsChange={setLawnDimensions}
              disabled={isLoading}
            />

            <div className="bg-white p-6 rounded-lg shadow-md">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold">Pelouse</h3>
                <div className="flex gap-2">
                  <button
                    onClick={clearPath}
                    className="px-3 py-1 text-sm bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors"
                    disabled={isLoading}
                  >
                    Effacer chemin
                  </button>
                  <button
                    onClick={resetMower}
                    className="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
                    disabled={isLoading}
                  >
                    Reset
                  </button>
                </div>
              </div>
              
              <div className="flex justify-center">
                <LawnGrid
                  dimensions={lawnDimensions}
                  mowers={[mowerPosition]}
                  paths={[mowerPath]}
                  onCellClick={handleCellClick}
                />
              </div>
            </div>
          </div>

          {/* Right Column - Controls */}
          <div>
            <MowerControls
              position={mowerPosition}
              onPositionChange={setMowerPosition}
              onExecuteCommand={handleExecuteCommand}
              onExecuteSequence={handleExecuteSequence}
              disabled={isLoading || apiStatus !== 'connected'}
            />

            {/* API Connection Controls */}
            <div className="mt-6 bg-white p-4 rounded-lg shadow-md">
              <h3 className="text-lg font-semibold mb-4">Connexion API</h3>
              <button
                onClick={checkApiHealth}
                className="w-full px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors disabled:opacity-50"
                disabled={apiStatus === 'checking'}
              >
                {apiStatus === 'checking' ? 'Vérification...' : 'Tester la connexion'}
              </button>
              <div className="text-xs text-gray-500 mt-2">
                Backend API: http://localhost:8080/api/mower
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
