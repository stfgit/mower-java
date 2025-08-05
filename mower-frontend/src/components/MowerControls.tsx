'use client';

import React, { useState } from 'react';
import { MowerPosition } from '@/types/mower';

interface MowerControlsProps {
  position: MowerPosition;
  onPositionChange: (position: MowerPosition) => void;
  onExecuteCommand: (command: string) => void;
  onExecuteSequence: (sequence: string) => void;
  disabled?: boolean;
}

export const MowerControls: React.FC<MowerControlsProps> = ({
  position,
  onPositionChange,
  onExecuteCommand,
  onExecuteSequence,
  disabled = false
}) => {
  const [commandSequence, setCommandSequence] = useState('GAGAGAGAA');

  const handleDirectionChange = (direction: 'N' | 'E' | 'S' | 'W') => {
    onPositionChange({ ...position, direction });
  };

  const handlePositionChange = (field: 'x' | 'y', value: number) => {
    onPositionChange({ ...position, [field]: Math.max(0, value) });
  };

  return (
    <div className="space-y-6">
      {/* Position Controls */}
      <div className="bg-white p-4 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-4">Position initiale</h3>
        
        <div className="grid grid-cols-3 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              X (Horizontal)
            </label>
            <input
              type="number"
              min="0"
              value={position.x}
              onChange={(e) => handlePositionChange('x', parseInt(e.target.value) || 0)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              disabled={disabled}
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Y (Vertical)
            </label>
            <input
              type="number"
              min="0"
              value={position.y}
              onChange={(e) => handlePositionChange('y', parseInt(e.target.value) || 0)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              disabled={disabled}
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Direction
            </label>
            <select
              value={position.direction}
              onChange={(e) => handleDirectionChange(e.target.value as 'N' | 'E' | 'S' | 'W')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              disabled={disabled}
            >
              <option value="N">Nord (↑)</option>
              <option value="E">Est (→)</option>
              <option value="S">Sud (↓)</option>
              <option value="W">Ouest (←)</option>
            </select>
          </div>
        </div>
      </div>

      {/* Manual Controls */}
      <div className="bg-white p-4 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-4">Contrôle manuel</h3>
        
        <div className="flex gap-2 mb-4">
          <button
            onClick={() => onExecuteCommand('G')}
            className="px-4 py-2 bg-yellow-500 text-white rounded-md hover:bg-yellow-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={disabled}
          >
            ↶ Gauche (G)
          </button>
          
          <button
            onClick={() => onExecuteCommand('A')}
            className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={disabled}
          >
            ↑ Avancer (A)
          </button>
          
          <button
            onClick={() => onExecuteCommand('D')}
            className="px-4 py-2 bg-yellow-500 text-white rounded-md hover:bg-yellow-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={disabled}
          >
            ↷ Droite (D)
          </button>
        </div>
      </div>

      {/* Sequence Mode */}
      <div className="bg-white p-4 rounded-lg shadow-md">
        <h3 className="text-lg font-semibold mb-4">Mode séquence</h3>
        
        <div className="flex gap-2">
          <input
            type="text"
            value={commandSequence}
            onChange={(e) => setCommandSequence(e.target.value.toUpperCase())}
            placeholder="Ex: GAGAGAGAA"
            className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            disabled={disabled}
          />
          
          <button
            onClick={() => onExecuteSequence(commandSequence)}
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={disabled || !commandSequence}
          >
            Exécuter
          </button>
        </div>
        
        <div className="text-xs text-gray-500 mt-2">
          Commandes: G (gauche), D (droite), A (avancer)
        </div>
      </div>
    </div>
  );
};