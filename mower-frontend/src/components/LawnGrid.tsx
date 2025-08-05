'use client';

import React from 'react';
import { MowerPosition, LawnDimensions } from '@/types/mower';

interface LawnGridProps {
  dimensions: LawnDimensions;
  mowers: MowerPosition[];
  onCellClick?: (x: number, y: number) => void;
  paths?: Array<{ x: number; y: number }[]>;
}

const DIRECTION_SYMBOLS = {
  N: '↑',
  E: '→',
  S: '↓',
  W: '←',
};

export const LawnGrid: React.FC<LawnGridProps> = ({ 
  dimensions, 
  mowers, 
  onCellClick,
  paths = [] 
}) => {
  const renderCell = (x: number, y: number) => {
    const mower = mowers.find(m => m.x === x && m.y === y);
    const isPath = paths.some(path => path.some(p => p.x === x && p.y === y));
    
    return (
      <div
        key={`${x}-${y}`}
        className={`
          w-8 h-8 border border-gray-300 flex items-center justify-center text-sm font-bold cursor-pointer
          transition-colors duration-200 hover:bg-gray-100
          ${mower ? 'bg-green-500 text-white' : ''}
          ${isPath && !mower ? 'bg-green-100' : ''}
        `}
        onClick={() => onCellClick?.(x, y)}
        title={`Position (${x}, ${y})`}
      >
        {mower && DIRECTION_SYMBOLS[mower.direction]}
      </div>
    );
  };

  return (
    <div className="inline-block border-2 border-gray-600 bg-green-50">
      {Array.from({ length: dimensions.height }, (_, y) => (
        <div key={y} className="flex">
          {Array.from({ length: dimensions.width }, (_, x) => 
            renderCell(x, dimensions.height - 1 - y)
          )}
        </div>
      ))}
      <div className="text-xs text-gray-500 mt-2 text-center">
        Cliquez sur une case pour placer la tondeuse
      </div>
    </div>
  );
};