'use client';

import React from 'react';
import { LawnDimensions } from '@/types/mower';

interface LawnSettingsProps {
  dimensions: LawnDimensions;
  onDimensionsChange: (dimensions: LawnDimensions) => void;
  disabled?: boolean;
}

export const LawnSettings: React.FC<LawnSettingsProps> = ({
  dimensions,
  onDimensionsChange,
  disabled = false
}) => {
  const handleDimensionChange = (field: 'width' | 'height', value: number) => {
    const newValue = Math.max(1, Math.min(20, value));
    onDimensionsChange({ ...dimensions, [field]: newValue });
  };

  return (
    <div className="bg-white p-4 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold mb-4">Configuration de la pelouse</h3>
      
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Largeur (1-20)
          </label>
          <input
            type="number"
            min="1"
            max="20"
            value={dimensions.width}
            onChange={(e) => handleDimensionChange('width', parseInt(e.target.value) || 1)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            disabled={disabled}
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Hauteur (1-20)
          </label>
          <input
            type="number"
            min="1"
            max="20"
            value={dimensions.height}
            onChange={(e) => handleDimensionChange('height', parseInt(e.target.value) || 1)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            disabled={disabled}
          />
        </div>
      </div>
      
      <div className="text-sm text-gray-500 mt-2">
        Pelouse actuelle: {dimensions.width} × {dimensions.height} cases
      </div>
    </div>
  );
};